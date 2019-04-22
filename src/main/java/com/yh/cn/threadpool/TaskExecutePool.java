package com.yh.cn.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * spring boot线程池
 */
@Configuration
public class TaskExecutePool {

    @Bean(name="taskExecutor")
    public TaskExecutor taskPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数：正常情况下开启的线程数量
        executor.setCorePoolSize(4);

        //当核心线程都在跑任务，还有多余的任务会存到此处
        executor.setQueueCapacity(6);

        //最大线程数：如果queueCapacity存满了，就会启动更多的线程，直到线程数达到maxPoolSize
        //。如果还有任务，则根据拒绝策略进行处理。
        executor.setMaxPoolSize(8);

        //线程空闲时间：空闲时间超过的会被销毁(不包括核心线程)
        executor.setKeepAliveSeconds(60);

        //线程名称前缀
        executor.setThreadNamePrefix("taskPool");


          //拒绝策略:不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //拒绝策略:直接抛异常拒绝
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}
