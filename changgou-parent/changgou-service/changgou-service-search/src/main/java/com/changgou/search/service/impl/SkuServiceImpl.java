package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ElasticsearchTemplate elasticsearchTemplate;

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
            Map<String,Object> specMap = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfoList);
    }

    /**
     * 暂时只做关键字搜索
     * @param searchMap {"keywords":"手机"}
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
        // 3. 设置条件 匹配查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name",keywords));
        // 4. 构建查询对象
        SearchQuery query = nativeSearchQueryBuilder.build();
        // 5. 执行查询
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(query, SkuInfo.class);
        // 6. 获取结果 封装返回
        // 6.1 当前页的记录
        List<SkuInfo> content = skuInfos.getContent();
        // 6.2 根据条件命中的总记录数
        long totalElements = skuInfos.getTotalElements();
        // 6.3 总页数
        int totalPages = skuInfos.getTotalPages();
        // 封装到 Map 中
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("rows",content);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);
        return resultMap;
    }
}
