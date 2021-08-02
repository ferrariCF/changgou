package com.changgou.goods.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/spec")
@CrossOrigin
public class SpecController extends AbstractCoreController<Spec>{

    private SpecService  specService;

    @Autowired
    public SpecController(SpecService  specService) {
        super(specService, Spec.class);
        this.specService = specService;
    }

    /**
     * 根据三级分类 id 查询规格列表
     * @param id
     * @return
     */
    @GetMapping("/category/{id}")
    public Result<List<Spec>> findByCategoryId(@PathVariable(name = "id") Integer id) {
        List<Spec> specList = specService.findByCategoryId(id);
        return Result.ok(specList);
    }
}
