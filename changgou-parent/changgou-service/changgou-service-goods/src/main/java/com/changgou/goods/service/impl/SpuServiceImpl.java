package com.changgou.goods.service.impl;

import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/****
 * @Author:admin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl extends CoreServiceImpl<Spu> implements SpuService {

    private SpuMapper spuMapper;

    @Autowired
    public SpuServiceImpl(SpuMapper spuMapper) {
        super(spuMapper, Spu.class);
        this.spuMapper = spuMapper;
    }
}
