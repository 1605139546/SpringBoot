package com.yh.cn.kafka;

import java.util.concurrent.ExecutionException;

public class TestMain {

    //使用spring-kafka的template发送一条消息 发送多条消息只需要循环多次即可
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(KafkaConfig.class);
//        KafkaTemplate<Integer, String> kafkaTemplate = (KafkaTemplate<Integer, String>) ctx.getBean("kafkaTemplate");
//        String data="this is a test message";
//        ListenableFuture<SendResult<Integer, String>> send = kafkaTemplate.send("topic-test", 1, data);
//        send.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
//            public void onFailure(Throwable throwable) {
//                System.out.println("==error=="+throwable);
//            }
//
//            public void onSuccess(SendResult<Integer, String> integerStringSendResult) {
//                System.out.println("==success=="+integerStringSendResult);
//            }
//        });
    }
}
