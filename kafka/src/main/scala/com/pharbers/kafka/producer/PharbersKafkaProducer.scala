package com.pharbers.kafka.producer

import java.net.InetAddress
import java.util.Properties
import java.util.concurrent.{Future, TimeUnit}

import com.pharbers.kafka.common.kafka_config_obj
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.tools.jline_embedded.internal.Log

/**
  * @ ProjectName micro-service-libs.com.pharbers.kafka.producer.PharbersKafkaProducer
  * @ author jeorch
  * @ date 19-6-11
  * @ Description: TODO
  */
object PharbersKafkaProducer {
    lazy val default_producer: PharbersKafkaProducer[String, Array[Byte]] = new PharbersKafkaProducer[String, Array[Byte]]()

    def apply: PharbersKafkaProducer[String, Array[Byte]] = default_producer
//    def apply[K, V]: PharbersKafkaProducer[K, V] = new PharbersKafkaProducer[K, V]()
}

class PharbersKafkaProducer[K, V] {

    lazy val config = new Properties()
    config.put("client.id", InetAddress.getLocalHost.getHostName)
    config.put("bootstrap.servers", kafka_config_obj.broker)
    config.put("acks", kafka_config_obj.acks)
    config.put("key.serializer", kafka_config_obj.keyDefaultSerializer)
    config.put("value.serializer", kafka_config_obj.valueDefaultSerializer)
    lazy val producer = new KafkaProducer[K, V](config)

    def produce(topic: String, key: K, value: V): Future[RecordMetadata] = {
        val record: ProducerRecord[K, V] = new ProducerRecord[K, V](topic, key, value)
        val future: Future[RecordMetadata] = producer.send(record,
            new Callback() {
                def onCompletion(metadata: RecordMetadata, e: Exception): Unit = {
                    if (e != null) Log.debug("Send failed for record {}", record, e) else Log.info("SUCCEED!")
                }
            })

        return future
    }

}
