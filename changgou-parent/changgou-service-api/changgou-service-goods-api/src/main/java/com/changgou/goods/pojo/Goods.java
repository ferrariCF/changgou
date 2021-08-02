package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author: lee
 * @date: 2021-08-02
 * 封装添加商品页面传来的数据
 **/
public class Goods implements Serializable {

    private Spu spu;
    private List<Sku> skuList;

    public Goods() {
    }

    public Goods(Spu spu, List<Sku> skuList) {
        this.spu = spu;
        this.skuList = skuList;
    }

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
