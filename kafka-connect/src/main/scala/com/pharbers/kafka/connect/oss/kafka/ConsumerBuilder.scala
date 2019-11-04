package com.pharbers.kafka.connect.oss.kafka

import java.time.Duration
import java.util

import com.pharbers.kafka.consumer.PharbersKafkaConsumer
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._
/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/09/26 11:06
  * @note 一些值得注意的地方
  */
class ConsumerBuilder[K, V](topic: String) extends Runnable{
    val consumer: KafkaConsumer[K, V] =  new PharbersKafkaConsumer[K, V](List(topic)).getConsumer
    val msgList = new util.LinkedList[V]()
    consumer.subscribe(List(topic).asJava)

    override def run(): Unit = {
        while(!Thread.currentThread().isInterrupted){
            consumer.poll(Duration.ofSeconds(1)).asScala.foreach(x => msgList.add(x.value()))
        }
        consumer.close()
    }

    def hasNext: Boolean ={
        !msgList.isEmpty
    }

    def next: V = {
        msgList.pollFirst()
    }

}
