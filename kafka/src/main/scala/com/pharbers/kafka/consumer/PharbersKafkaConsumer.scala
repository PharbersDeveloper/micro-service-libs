package com.pharbers.kafka.consumer

/**
  * @ ProjectName micro-service-libs.com.pharbers.kafka.consumer.BasicConsumeLoop
  * @ author jeorch
  * @ date 19-6-11
  * @ Description: TODO
  */

import java.net.InetAddress
import java.util.{Collections, Properties}
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean

import com.pharbers.kafka.common.kafka_config_obj

import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}

import scala.tools.jline_embedded.internal.Log

class PharbersKafkaConsumer[K, V](val topics: List[String], val timeoutMs: Long = Long.MaxValue, val consumeTimes: Int = Int.MaxValue,
                             val process: ConsumerRecord[K, V] => Unit = {record: ConsumerRecord[K, V] => Log.info("===process>>>" + record.key() + ":" + new String(record.value().asInstanceOf[Array[Byte]]))}) extends Runnable {

    val config = new Properties()
    config.put("client.id", InetAddress.getLocalHost.getHostName)
    config.put("group.id", kafka_config_obj.group)
    config.put("bootstrap.servers", kafka_config_obj.broker)
    config.put("key.deserializer", kafka_config_obj.keyDefaultDeserializer)
    config.put("value.deserializer", kafka_config_obj.valueDefaultDeserializer)

    final private val CONSUMER = new KafkaConsumer[K, V](config)
    final private val SHUTDOWN = new AtomicBoolean(false)
    final private val CONSUME_TIMES = new Semaphore(consumeTimes)

    override def run(): Unit = {
        try {
            if (topics.nonEmpty) CONSUMER.subscribe(topics.asJava) else CONSUMER.subscribe(kafka_config_obj.topics.toList.asJava)
            Log.info("Origin CONSUME_TIMES=" + CONSUME_TIMES.availablePermits())
            while ( {
                !SHUTDOWN.get
            }) {
                val records = CONSUMER.poll(timeoutMs)
                records.asScala.foreach(process)
                if (!records.isEmpty) CONSUME_TIMES.acquire()
                Log.info("The rest of CONSUME_TIMES=" + CONSUME_TIMES.availablePermits())
                if (CONSUME_TIMES.availablePermits() <= 0) shutdown()
            }
        } finally {
            shutdown()
        }
    }

    @throws[InterruptedException]
    def shutdown(): Unit = {
        CONSUMER.close()
        SHUTDOWN.set(true)
    }
}
