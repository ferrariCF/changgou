package com.changgou.goods.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/spu")
@CrossOrigin
public class SpuController extends AbstractCoreController<Spu>{

    private SpuService  spuService;

    @Autowired
    public SpuController(SpuService  spuService) {
        super(spuService, Spu.class);
        this.spuService = spuService;
    }

    /**
     * 保存商品信息
     * @param goods 封装页面传来的商品数据
     * @return
     */
    @PostMapping("/save")
    public Result<Goods> save(@RequestBody Goods goods){
        spuService.save(goods);
        return Result.ok();
    }

    /**
     * 根据 spu 的 id 回显商品数据
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}")
    public Result<Goods> findGoodsById(@PathVariable(name = "id") Long id){
        Goods goods = spuService.findGoodsById(id);
        return Result.ok(goods);
    }
}
