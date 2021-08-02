package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.core.service.impl.CoreServiceImpl;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl extends CoreServiceImpl<Spu> implements SpuService {

    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    public SpuServiceImpl(SpuMapper spuMapper) {
        super(spuMapper, Spu.class);
        this.spuMapper = spuMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Goods goods) {
        if (null != goods) {
            // 1. 添加spu
            Spu spu = goods.getSpu();

            // 判断 spu 是否有 id
            if (spu.getId() != null) {
                // spu 有 id, 则更新
                spuMapper.updateByPrimaryKeySelective(spu);
                // 先删除原有的 sku ，再新增
                Sku condition = new Sku();
                condition.setSpuId(spu.getId());
                skuMapper.delete(condition);
            }else{
                // 没有 id 则新增
                // 1.1 生成 spu 的 id
                long id = idWorker.nextId();
                spu.setId(id);
                spuMapper.insertSelective(spu);
            }

            // 2. 添加sku
            List<Sku> skuList = goods.getSkuList();
            if (null != skuList) {
                for (Sku sku : skuList) {
                    // 2.1 生成 sku id
                    long skuId = idWorker.nextId();
                    sku.setId(skuId);
                    // 2.2 设置 sku name
                    // 格式： 华为mate40 黑色 256G
                    StringBuilder name = new StringBuilder(spu.getName());
                    String spec = sku.getSpec();
                    Map<String,String> map = JSON.parseObject(spec, Map.class);
                    if (map != null) {
                        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                            String value = stringStringEntry.getValue();
                            name.append(" ").append(value);
                        }
                    }
                    sku.setName(name.toString());
                    // 2.3 设置 create_time, update_time
                    sku.setCreateTime(new Date());
                    sku.setUpdateTime(sku.getCreateTime());
                    // 2.4 设置 spu_id
                    sku.setSpuId(spu.getId());
                    // 2.5 设置 category_id, category_name
                    Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
                    if (category != null) {
                        sku.setCategoryId(category.getId());
                        sku.setCategoryName(category.getName());
                    }
                    // 2.6 设置 brand_name
                    Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
                    if (brand != null){
                        sku.setBrandName(brand.getName());
                    }
                    skuMapper.insertSelective(sku);
                }
            }
        }
    }

    /**
     * 根据 spu 的 id 查询商品数据
     * @param id
     * @return
     */
    @Override
    public Goods findGoodsById(Long id) {
        // 1. 根据 id 获取 spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 1. 根据 spu_id 获取 skuList
        Sku condition = new Sku();
        condition.setSpuId(id);
        List<Sku> skuList = skuMapper.select(condition);

        return new Goods(spu,skuList);
    }
}
