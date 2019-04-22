package com.yh.cn.config.jsonp;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;


/**
 * 在4.1版本以后的SpringMVC中，为我们提供了一个AbstractJsonpResponseBodyAdvice的类用来支持jsonp的数据
 */
@ControllerAdvice(basePackages = "com.yh.cn.controller")
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {

    public JsonpAdvice() {
        super("callback", "jsonp");
    }
} 
