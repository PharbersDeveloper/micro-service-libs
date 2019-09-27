package com.pharbers.kafka.connect.oss.kafka

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
class ConsumerBuilder {
    def build[K, V](topic: String): KafkaConsumer[K, V] = {
        val consumer = new PharbersKafkaConsumer[K, V](List(topic)).getConsumer
        consumer.subscribe(List(topic).asJava)
        consumer
    }
}
