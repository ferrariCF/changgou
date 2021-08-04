package com.changgou.search.service;

import java.util.Map;

/**
 * @author: lee
 * @date: 2021-08-04
 **/
public interface SkuService {
    void importSku();

    Map<String, Object> search(Map<String, String> searchMap);
}
