package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: lee
 * @date: 2021-08-04
 **/
@Repository//可以不加
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
