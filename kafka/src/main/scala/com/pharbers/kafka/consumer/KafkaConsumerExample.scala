//package com.pharbers.kafka.consumer
//
//import java.util
//import java.util.Properties
//import java.net.InetAddress
//import scala.collection.JavaConverters._
//
//import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
//import org.apache.kafka.common.serialization.StringDeserializer
//
///**
//  * @ ProjectName micro-service-libs.com.pharbers.kafka.consumer.KafkaProperties
//  * @ author jeorch
//  * @ date 19-6-6
//  * @ Description: TODO
//  */
//object KafkaConsumerExample extends App {
//
//    val config = new Properties()
//    config.put("client.id", InetAddress.getLocalHost.getHostName)
//    config.put("group.id", "foo")
////    config.put("bootstrap.servers", "59.110.31.50:9092")
//    config.put("bootstrap.servers", "localhost:9092")
//    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
//    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
//    val kc = new KafkaConsumer(config)
//    val topics = new util.ArrayList[String]()
//    topics.add("test")
//    println(topics)
//    kc.subscribe(topics)
//    while (true) {
//        val records = kc.poll(Long.MaxValue)
//        records.asScala.foreach(process)
//        kc.commitSync
//    }
//
//
//    def process[K, V](record: ConsumerRecord[K, V]): Unit = {
//        println("===process>>>" + record.key() + ":" + record.value())
//    }
//
//}
//
