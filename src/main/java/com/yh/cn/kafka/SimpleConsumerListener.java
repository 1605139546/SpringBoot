package com.yh.cn.kafka;

//public class SimpleConsumerListener {
//
//    private final static Logger logger = LoggerFactory.getLogger(SimpleConsumerListener.class);
//    private final CountDownLatch latch1 = new CountDownLatch(1);
//
//    @KafkaListener(id = "foo", topics = "topic-test")
//    public void listen(String  records) {
//        //do something here
//        System.out.println("=========="+records);
//        this.latch1.countDown();
//    }
//}