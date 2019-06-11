//package com.pharbers.kafka.producer
//
//import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
//import java.net.InetAddress
//import java.util.Properties
//import java.util.concurrent.{Future, TimeUnit}
//
//import org.apache.kafka.common.serialization.StringSerializer
//
//import scala.tools.jline_embedded.internal.Log
//
//
///**
//  * @ ProjectName micro-service-libs.com.pharbers.kafka.producer.KafkaProducerExample
//  * @ author jeorch
//  * @ date 19-6-11
//  * @ Description: TODO
//  */
//object KafkaProducerExample extends App {
//
//    val config = new Properties()
//    config.put("client.id", InetAddress.getLocalHost.getHostName)
//    config.put("bootstrap.servers", "localhost:9092")
//    config.put("acks", "all")
//    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//    config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//    new StringSerializer()
//    val producer = new KafkaProducer[String, String](config)
//    val record: ProducerRecord[String, String] = new ProducerRecord[String, String]("test", "key", "aha123")
//    val future: Future[RecordMetadata] = producer.send(record,
//    new Callback() {
//        def onCompletion(metadata: RecordMetadata, e: Exception): Unit = {
//            if (e != null) Log.debug("Send failed for record {}", record, e) else Log.info("SUCCEED!")
//        }
//    })
//
//    Log.info("Sent record is ", future.get(10, TimeUnit.SECONDS))
//
//}
