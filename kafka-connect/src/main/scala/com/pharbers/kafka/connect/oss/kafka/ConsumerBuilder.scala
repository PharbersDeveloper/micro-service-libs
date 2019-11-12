package com.pharbers.kafka.connect.oss.kafka

import java.io.{File, FileOutputStream, FileWriter}
import java.time.Duration
import java.util
import java.util.Scanner

import com.pharbers.kafka.consumer.PharbersKafkaConsumer
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.codehaus.jackson.map.ObjectMapper

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/09/26 11:06
  * @note 一些值得注意的地方
  */
class ConsumerBuilder[K, V <: SpecificRecordBase](topic: String, classTag: Class[V]) extends Runnable{
    val path = new File("/usr/share/logs-path/msgList")
    val mapping = new ObjectMapper()
    val consumer: KafkaConsumer[K, V] =  new PharbersKafkaConsumer[K, V](List(topic)).getConsumer
    val msgList = new util.LinkedList[V]()
    if(path.exists()){
        val scanner = new Scanner(path)
        while (scanner.hasNext()){
            val value = classTag.newInstance()
            mapping.readValue(scanner.nextLine(), classOf[java.util.Map[String, String]]).asScala.foreach(x => value.put(x._1, x._2))
            msgList.add(value)
        }
        path.delete()
    }
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
        path.delete()
        path.createNewFile()
        val write = new FileWriter(path)
        msgList.asScala.foreach(x => write.write(x.toString + "\n"))
        write.flush()
        write.close()
        msgList.pollFirst()
    }
}
