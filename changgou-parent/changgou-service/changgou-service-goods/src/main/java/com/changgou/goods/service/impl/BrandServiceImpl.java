package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: lee
 * @date: 2021-07-30
 **/
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 查询所有品牌
     *
     * @return
     */
    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 根据 id 查询品牌数据
     *
     * @param id
     * @return
     */
    @Override
    public Brand findById(Integer id) {
        // 根据主键查询
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 添加品牌
     *
     * @param brand
     */
    @Override
    public void add(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    /**
     * 根据 id 修改品牌数据
     *
     * @param brand
     */
    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    /**
     * 根据 id 删除品牌
     *
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 条件查询
     *
     * @param brand
     * @return
     */
    @Override
    public List<Brand> search(Brand brand) {
        Example example = createExample(brand);
        // 3. 执行条件查询
        List<Brand> brandList = brandMapper.selectByExample(example);

        return brandList;
    }

    /**
     * 条件创建
     * @param brand
     * @return
     */
    private Example createExample(Brand brand) {
        Example example = new Example(Brand.class);
        // 1. 判断是否为空
        if (brand != null) {
            // 2. 根据不同情况组装条件对象 example
            Example.Criteria criteria = example.createCriteria();

            if (!StringUtils.isEmpty(brand.getName())) {
                criteria.andLike("name","%" + brand.getName() + "%");
            }
            if (!StringUtils.isEmpty(brand.getLetter())) {
                criteria.andEqualTo("name",brand.getLetter());
            }
        }
        return example;
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Brand> findByPage(Integer page, Integer size) {
        // 1. 开始分页
        PageHelper.startPage(page,size);
        // 2. 执行查询的sql (查询所有)
        List<Brand> brandList = brandMapper.selectAll();
        // 3. 封装到pageInfo对象中
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        return pageInfo;
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param brand
     * @return
     */
    @Override
    public PageInfo<Brand> findByPage(Integer page, Integer size, Brand brand) {
        // 1. 开始分页
        PageHelper.startPage(page,size);
        // 2. 执行查询的sql (条件查询)
        Example example = createExample(brand);
        List<Brand> brandList = brandMapper.selectByExample(example);
        // 3. 封装到pageInfo对象中
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        return pageInfo;
    }
}
