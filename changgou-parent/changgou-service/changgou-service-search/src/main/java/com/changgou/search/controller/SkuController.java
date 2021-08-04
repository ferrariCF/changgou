package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: lee
 * @date: 2021-08-04
 **/
@RestController
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 将数据库中的数据查询出来导入 es
     * @return
     */
    @GetMapping("/import")
    public Result importToEs() {
        skuService.importSku();
        return Result.ok();
    }

    /**
     * 根据条件搜索
     * @param searchMap 搜索条件的封装对象
     * @return 封装的数据对象 map 里面包含（当前页的记录 总页数，总记录数......）
     */
    @PostMapping
    public Result<Map<String,Object>> search(@RequestBody(required = false) Map<String,String> searchMap){
        if (searchMap == null) {
            searchMap = new HashMap<>();
        }
        Map<String,Object> resultMap = skuService.search(searchMap);
        return Result.ok(resultMap);
    }
}
