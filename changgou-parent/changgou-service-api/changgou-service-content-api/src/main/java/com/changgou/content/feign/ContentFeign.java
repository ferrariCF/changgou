package com.changgou.content.feign;

import com.changgou.content.pojo.Content;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author: lee
 * @date: 2021-08-03
 **/
@FeignClient(name = "content",path = "/content")
public interface ContentFeign {
    /**
     * 根据广告分类的 id 获取广告分类下的所有的广告列表数据
     * @param id 广告分类的 id
     * @return
     */
    @GetMapping("/list/category/{id}")
    Result<List<Content>> findByCategoryId(@PathVariable(name = "id") Long id);
}
