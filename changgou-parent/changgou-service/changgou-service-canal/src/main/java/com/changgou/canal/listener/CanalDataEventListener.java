package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @author: lee
 * @date: 2021-08-03
 **/
@CanalEventListener
public class CanalDataEventListener {

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /***
     * 自定义数据修改监听
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",
            schema = "changgou_content",
            table = {"tb_content_category", "tb_content"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE, CanalEntry.EventType.INSERT})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        // 1. 获取 category_id 的值
        String categoryId = getColumnValue(eventType, rowData);
        // 2. 通过 feign 调用广告微服务，根据 category_id 获取该分类下所有广告列表
        Result<List<Content>> result = contentFeign.findByCategoryId(Long.valueOf(categoryId));
        List<Content> contentList = result.getData();
        // 3. 将数据存到 redis 中
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(contentList));
    }

    /**
     * 获取 category_id 的值
     * @param eventType
     * @param rowData
     * @return
     */
    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = "";
        // 1. 事件类型判断
        if (eventType == CanalEntry.EventType.DELETE) {
            // 如果是 delete, 获取 before 的数据
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    break;
                }
            }
        }else{
            // 如果是 insert 和 update, 那么获取 after 的数据
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList) {
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    break;
                }
            }
        }
        return categoryId;
    }
}
