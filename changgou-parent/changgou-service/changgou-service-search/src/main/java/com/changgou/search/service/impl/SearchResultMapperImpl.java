package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: lee
 * @date: 2021-08-07
 **/
public class SearchResultMapperImpl implements SearchResultMapper {
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
        List<T> content = new ArrayList<>();
        SearchHits hits = searchResponse.getHits();

        if (hits == null || hits.getTotalHits() <= 0){
            return new AggregatedPageImpl<T>(content);
        }

        for (SearchHit hit : hits) {
            // 获取非高亮数据
            String sourceAsString = hit.getSourceAsString();
            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
            // 获取高亮数据
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null
                    && highlightFields.get("name") != null
                    && highlightFields.get("name").getFragments() != null
                    && highlightFields.get("name").getFragments().length > 0 ) {
                // 获取高亮字段为 name 的高亮对象
                HighlightField highlightField = highlightFields.get("name");
                // 获取高亮碎片
                Text[] fragments = highlightField.getFragments();
                StringBuffer sb = new StringBuffer();
                for (Text fragment : fragments) {
                    String string = fragment.string();
                    sb.append(string);
                }
                skuInfo.setName(sb.toString());
            }
            // 将高亮数据设置到 content 中
            content.add((T) skuInfo);
        }
        // 总记录条数
        long totalHits = hits.getTotalHits();
        // 聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        // 游标id
        String scrollId = searchResponse.getScrollId();
        return new AggregatedPageImpl<T>(content, pageable, totalHits, aggregations, scrollId);
    }
}
