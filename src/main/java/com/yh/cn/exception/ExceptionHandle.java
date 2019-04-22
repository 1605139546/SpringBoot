package com.yh.cn.exception;

import com.yh.cn.util.Result;
import com.yh.cn.util.ResultUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局捕获异常处理
 */
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = GirlException.class)
    @ResponseBody
    public Result handle(Exception e) {
        if (e instanceof GirlException) {
            GirlException girlException = (GirlException) e;
            return ResultUtils.error(girlException.getCode(), e.getMessage());
        }
        return ResultUtils.error(-1, "未知错误");
    }
}