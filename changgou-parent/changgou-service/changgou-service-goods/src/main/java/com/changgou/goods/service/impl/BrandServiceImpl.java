package com.changgou.goods.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: lee
 * @date: 2021-07-30
 **/
@Service
public class BrandServiceImpl extends CoreServiceImpl<Brand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    //注解可以修饰在构造函数中
    //当修饰的构造函数的参数类型 spring容器有该参数类型的BEAN 就会进行注入
    @Autowired
    public BrandServiceImpl(BrandMapper brandMapper) {
        super(brandMapper, Brand.class);
        //this.brandMapper=brandMapper;
    }

}

