package com.changgou.goods.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: lee
 * @date: 2021-07-30
 **/
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception e){
        // 异常信息记录到日志
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR,e.getMessage());
    }
}
