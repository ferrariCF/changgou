package com.changgou.goods.dao;

import com.changgou.goods.pojo.Para;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:ParaDao
 * @Date 2019/6/14 0:12
 *****/
public interface ParaMapper extends Mapper<Para> {
    /**
     * 根据三级分类 id 查询参数列表
     * @param id
     * @return
     */
    @Select("select tbp.* from tb_category tbc " +
            "join tb_para tbp on tbc.template_id=tbp.template_id " +
            "where tbc.id=#{id}")
    List<Para> findByCategoryId(Integer id);
}
