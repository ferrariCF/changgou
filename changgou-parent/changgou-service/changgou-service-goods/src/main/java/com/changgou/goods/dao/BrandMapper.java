package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:BrandDao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {
    /**
     * 根据三级分类 id 查询品牌列表
     * @param id
     * @return
     */
    @Select("select tbb.* from tb_category_brand tcb join tb_brand tbb on tcb.brand_id=tbb.id where tcb.category_id=#{id}")
    List<Brand> findBrandByCategory(Integer id);
}
