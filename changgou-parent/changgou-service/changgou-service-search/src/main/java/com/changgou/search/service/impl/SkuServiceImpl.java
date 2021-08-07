package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: lee
 * @date: 2021-08-04
 **/
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 将数据库中的数据查询出来导入 es
     */
    @Override
    public void importSku() {
        // 1. 使用 feign 调用商品微服务，获取商品数据
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        List<Sku> skuList = result.getData();
        // 2. 将数据存储到 es 中
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuList), SkuInfo.class);
        // 将 spec 映射到 specMap 中
        for (SkuInfo skuInfo : skuInfoList) {
            String spec = skuInfo.getSpec();
            Map<String, Object> specMap = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfoList);
    }

    /**
     * 暂时只做关键字搜索
     *
     * @param searchMap {"keywords":"华为","category":"你点击的分类的值","brand":"你点击到的品牌的值",
     *                  "spec_网络制式":"电信2G","price":"0-500","sortField":"price","sortRule":"DESC",
     *                  "pageNum":"1"}
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        // 1. 获取关键字的值
        String keywords = searchMap.get("keywords");
        if (StringUtils.isBlank(keywords)) {
            keywords = "华为";
        }
        // 2. 创建查询对象的构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 2.1 设置根据分类的分组查询条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategoryGroup").field("categoryName").size(10000));
        // 2.2 设置根据品牌的分组查询条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandGroup").field("brandName").size(10000));
        // 2.3 设置根据规格的分组查询条件
        // 字段设置为: spec.keyword, 当搜索的时候实现分组查询的时候是不需要进行分词的
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecGroup").field("spec.keyword").size(100000));

        // 3. 设置条件 匹配查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
        // 3.1 过滤查询
        // 多条件组合查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 商品分类过滤
        String category = searchMap.get("category");
        if (!StringUtils.isEmpty(category)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName",category));
        }
        // 商品品牌过滤
        String brand = searchMap.get("brand");
        if (!StringUtils.isEmpty(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName",brand));
        }
        // 商品规格过滤
        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.startsWith("spec_")) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword",value));
            }
        }
        // 价格区间过滤(范围)
        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] split = price.split("-");
            if ("*".equals(split[1])){
                // 3000元以上-->3000-*
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }else{
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]).lte(split[1]));
            }
        }
        // 过滤
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);

        // 3.2 分页查询，设置分页对象，指定当前页码和每页条数
        String pageNumString = searchMap.get("pageNum");
        Integer pageNum = 1;
        if (!StringUtils.isEmpty(pageNumString)) {
            pageNum = Integer.valueOf(pageNumString);
        }
        Integer size = 20;
        // 设置分页对象
        Pageable pageable = PageRequest.of(pageNum - 1,size);
        nativeSearchQueryBuilder.withPageable(pageable);

        // 3.3 设置排序
        // 排序字段
        String sortField = searchMap.get("sortField");
        // 排序规则
        String sortRule = searchMap.get("sortRule");
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }

        // 4. 构建查询对象
        SearchQuery query = nativeSearchQueryBuilder.build();
        // 5. 执行查询
        AggregatedPage<SkuInfo> skuPage = esTemplate.queryForPage(query, SkuInfo.class);
        // 6. 获取结果 封装返回
        // 6.1 当前页的记录
        List<SkuInfo> content = skuPage.getContent();
        // 6.2 根据条件命中的总记录数
        long totalElements = skuPage.getTotalElements();
        // 6.3 总页数
        int totalPages = skuPage.getTotalPages();
        // 6.4 分组结果(分类列表)
        List<String> categoryList = getGroupNameList(skuPage, "skuCategoryGroup");
        // 6.5 分组结果(品牌列表)
        List<String> brandList = getGroupNameList(skuPage, "skuBrandGroup");
        // 6.6 获取规格列表和规格值列表 map:{"规格名1":[值1,值2]}
        StringTerms stringTerms = (StringTerms) skuPage.getAggregation("skuSpecGroup");
        Map<String, Set<String>> specMap = getSpecSetMap(stringTerms);

        // 封装到 Map 中
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", content);
        resultMap.put("total", totalElements);
        resultMap.put("totalPages", totalPages);
        resultMap.put("categoryList", categoryList);
        resultMap.put("brandList", brandList);
        resultMap.put("specMap", specMap);
        return resultMap;
    }

    /**
     * 解析从 es 中获取的规格数据
     * @param stringTerms
     * @return Map<String, Set<String>> key 为规格名, set集合为该规格名下面的值
     */
    private Map<String, Set<String>> getSpecSetMap(StringTerms stringTerms) {
        Map<String,Set<String>> specMap = new HashMap<>();

        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                Map<String,String> map = JSON.parseObject(bucket.getKeyAsString(), Map.class);
                //{"手机屏幕尺寸":"5.5寸","网络":"电信4G","颜色":"白","测试":"s11","机身内存":"128G","存储":"16G","像素":"300万像素"}
                //{"手机屏幕尺寸":"5.0寸","网络":"电信4G","颜色":"白","测试":"s11","机身内存":"128G","存储":"16G","像素":"300万像素"}
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Set<String> values = specMap.get(key);
                    if (values == null) {
                        values = new HashSet<>();
                    }
                    values.add(value);
                    specMap.put(key,values);
                }
            }
        }
        return specMap;
    }

    /**
     * 获取组名的列表
     *
     * @param skuPage  查询结果
     * @param skuGroup 分组查询的标识名
     * @return
     */
    private List<String> getGroupNameList(AggregatedPage<SkuInfo> skuPage, String skuGroup) {
        List<String> groupNameList = new ArrayList<>();
        StringTerms stringTerms = (StringTerms) skuPage.getAggregation(skuGroup);
        if (stringTerms != null) {
            stringTerms.getBuckets().forEach(bucket -> groupNameList.add(bucket.getKeyAsString()));
        }
        return groupNameList;
    }
}
