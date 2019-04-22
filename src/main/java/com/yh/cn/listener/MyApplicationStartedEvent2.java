package com.yh.cn.listener;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

public class MyApplicationStartedEvent2 implements ApplicationListener<ApplicationPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationStartedEvent) {
        System.out.println("启动了2");
    }
}
