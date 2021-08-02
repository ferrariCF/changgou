package com.changgou.goods.dao;
import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:SpecDao
 * @Date 2019/6/14 0:12
 *****/
public interface SpecMapper extends Mapper<Spec> {
    /**
     * 根据三级分类 id 查询规格列表
     * @param id
     * @return
     */
    @Select("select tbs.* from tb_category tbc " +
            "join tb_spec tbs on tbc.template_id=tbs.template_id " +
            "where tbc.id=#{id}")
    List<Spec> findByCategoryId(Integer id);
}
