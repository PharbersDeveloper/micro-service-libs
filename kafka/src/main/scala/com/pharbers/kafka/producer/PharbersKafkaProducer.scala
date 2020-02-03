package com.pharbers.kafka.producer

import java.net.InetAddress
import java.util.Properties
import java.util.concurrent.Future

import com.pharbers.kafka.common.kafka_config_obj
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.kafka.clients.producer._
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
    config.put(ProducerConfig.CLIENT_ID_CONFIG, InetAddress.getLocalHost.getHostName)
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_config_obj.broker)
    config.put(ProducerConfig.ACKS_CONFIG, kafka_config_obj.acks)
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafka_config_obj.keyDefaultSerializer)
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafka_config_obj.valueDefaultSerializer)
    config.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafka_config_obj.schemaRegistryUrl)
    config.put("security.protocol", kafka_config_obj.securityProtocol)
    config.put("ssl.endpoint.identification.algorithm", kafka_config_obj.sslAlgorithm)
    config.put("ssl.truststore.location", kafka_config_obj.sslTruststoreLocation)
    config.put("ssl.truststore.password", kafka_config_obj.sslTruststorePassword)
    config.put("ssl.keystore.location", kafka_config_obj.sslKeystoreLocation)
    config.put("ssl.keystore.password", kafka_config_obj.sslKeystorePassword)
    val producer = new KafkaProducer[K, V](config)


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
