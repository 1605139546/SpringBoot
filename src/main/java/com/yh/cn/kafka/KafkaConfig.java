package com.yh.cn.kafka;

//@Configuration
//@EnableKafka
//public class KafkaConfig {
//
//    //topic config Topic的配置开始
//    @Bean
//    public KafkaAdmin admin() {
//        Map<String, Object> configs = new HashMap<String, Object>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
//        return new KafkaAdmin(configs);
//    }
//
//    @Bean
//    public NewTopic topic1() {
//        return new NewTopic("foo", 10, (short) 2);
//    }
//    //topic的配置结束
//
//    //producer config start
//    @Bean
//    public ProducerFactory<Integer, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<Integer,String>(producerConfigs());
//    }
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<String,Object>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put("acks", "all");
//        props.put("retries", 0);
//        props.put("batch.size", 16384);
//        props.put("linger.ms", 1);
//        props.put("buffer.memory", 33554432);
//        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        return props;
//    }
//    @Bean
//    public KafkaTemplate<Integer, String> kafkaTemplate() {
//        return new KafkaTemplate<Integer, String>(producerFactory());
//    }
//    //producer config end
//
//
//    //consumer config start
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<Integer,String> kafkaListenerContainerFactory(){
//        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<Integer, String>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<Integer,String> consumerFactory(){
//        return new DefaultKafkaConsumerFactory<Integer, String>(consumerConfigs());
//    }
//
//
//    @Bean
//    public Map<String,Object> consumerConfigs(){
//        HashMap<String, Object> props = new HashMap<String, Object>();
//        props.put("bootstrap.servers", "localhost:9092");
//        props.put("group.id", "test");
//        props.put("enable.auto.commit", "true");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        return props;
//    }
//    //consumer config end
//
//    @Bean
//    public SimpleConsumerListener simpleConsumerListener(){
//        return new SimpleConsumerListener();
//    }
//}