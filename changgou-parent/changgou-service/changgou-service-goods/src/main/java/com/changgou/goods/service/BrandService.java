package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author: lee
 * @date: 2021-07-30
 **/
public interface BrandService {
    /**
     * 查询所有品牌
     * @return
     */
    List<Brand> findAll();

    /**
     * 根据 id 查询品牌数据
     * @param id
     * @return
     */
    Brand findById(Integer id);

    /**
     * 添加品牌
     * @param brand
     */
    void add(Brand brand);

    /**
     * 根据 id 修改品牌数据
     * @param brand
     */
    void update(Brand brand);

    /**
     * 根据 id 删除品牌
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 条件查询
     * @param brand
     * @return
     */
    List<Brand> search(Brand brand);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findByPage(Integer page, Integer size);

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param brand
     * @return
     */
    PageInfo<Brand> findByPage(Integer page, Integer size, Brand brand);
}
