package com.yh.cn;

import com.yh.cn.listener.MyApplicationStartedEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot 应用启动类
 */
@SpringBootApplication
@ServletComponentScan
@EnableCaching
@EnableAsync
@ComponentScan(basePackages = "com.yh.cn")
@EnableAutoConfiguration(exclude = {
        JpaRepositoriesAutoConfiguration.class//禁止springboot自动加载持久化bean
})
//@EnableScheduling
public class Application /*extends SpringBootServletInitializer*/ {

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(Application.class);
//
//    }
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new MyApplicationStartedEvent());
        application.run(args);
        SpringApplication.run(Application.class, args);
    }
}
