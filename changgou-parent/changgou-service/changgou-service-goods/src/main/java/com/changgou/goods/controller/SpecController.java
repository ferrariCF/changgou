package com.changgou.goods.controller;

import com.changgou.core.AbstractCoreController;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/spec")
@CrossOrigin
public class SpecController extends AbstractCoreController<Spec>{

    private SpecService  specService;

    @Autowired
    public SpecController(SpecService  specService) {
        super(specService, Spec.class);
        this.specService = specService;
    }
}
