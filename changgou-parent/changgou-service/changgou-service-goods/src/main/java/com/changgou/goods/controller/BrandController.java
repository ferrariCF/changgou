package com.changgou.goods.controller;

import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: lee
 * @date: 2021-07-30
 **/
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 查询所有品牌
     * @return
     */
    @GetMapping
    public Result<List<Brand>> findAll(){
        List<Brand> brandList = brandService.findAll();
        return new Result<>(true, StatusCode.OK,"查询所有品牌信息成功",brandList);
    }

    /**
     * 根据 id 查询品牌，获取品牌数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Brand> findById(@PathVariable(name = "id") Integer id){
        Brand brand = brandService.findById(id);
        return new Result<>(true,StatusCode.OK,"根据id查询品牌成功",brand);
    }

    /**
     * 添加品牌
     * @param brand
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result(true,StatusCode.OK,"添加品牌成功");
    }

    /**
     * 根据 id 修改品牌数据
     * @param id
     * @param brand
     * @return
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable(name = "id") Integer id,@RequestBody Brand brand){
        brand.setId(id);
        brandService.update(brand);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /**
     * 根据 id 删除品牌
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable(name = "id") Integer id){
        brandService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 条件查询
     * @param brand 将查询条件封装到 brand 中
     * @return
     */
    @PostMapping("/search")
    public Result<List<Brand>> search(@RequestBody Brand brand) {
        List<Brand> brandList = brandService.search(brand);
        return new Result<>(true,StatusCode.OK,"搜索成功",brandList);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Brand>> findByPage(@PathVariable(name = "page") Integer page,
                                              @PathVariable(name = "size") Integer size){
        PageInfo<Brand> pageInfo = brandService.findByPage(page,size);
        return new Result<>(true,StatusCode.OK,"分页查询成功",pageInfo);
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param brand
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Brand>> findByPage(@PathVariable(name = "page") Integer page,
                                              @PathVariable(name = "size") Integer size,
                                              @RequestBody Brand brand){
        PageInfo<Brand> pageInfo = brandService.findByPage(page,size,brand);
        return new Result<>(true,StatusCode.OK,"分页查询成功",pageInfo);
    }
}
