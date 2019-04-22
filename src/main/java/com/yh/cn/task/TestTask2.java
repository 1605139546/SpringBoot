package com.yh.cn.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 方法二
 */
//@Configuration //证明这个类是一个配置文件
//@EnableScheduling //打开quartz定时器总开关
public class TestTask2 {

//    @Scheduled(cron = "0/5 * * * * *")
    public void timer(){
        //获取当前时间
        LocalDateTime localDateTime =LocalDateTime.now();
        System.out.println("当前时间TestTask2为:" + localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
