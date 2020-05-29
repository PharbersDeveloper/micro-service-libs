package com.pharbers.kafka.connect.oss.kafka

import java.io.{File, FileOutputStream, FileWriter}
import java.time.Duration
import java.util
import java.util.Scanner

import com.pharbers.kafka.consumer.PharbersKafkaConsumer
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.InterruptException
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
  * @note 如果kafka非常多，在这儿一直积压，可能会造成oom
  *       如果消费和生产都特别快，这儿会写本地io，可能造成性能瓶颈
  *       变成分布式connector后，这儿消费是基于消费组的，要是kafka分片小于worker数量，就会有worker空转
  */
class ConsumerBuilder[K, V <: SpecificRecordBase](topic: String, classTag: Class[V]) extends Runnable{
    val path = new File("/usr/share/logs-path/msgList")
    val mapping = new ObjectMapper()
    val consumer: KafkaConsumer[K, V] =  new PharbersKafkaConsumer[K, V](List(topic)).getConsumer
    var keys: List[K] = List()
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
        try {
            while(!Thread.currentThread().isInterrupted){
                consumer.poll(Duration.ofSeconds(1)).asScala.foreach(x => {
                    if (!keys.contains(x.key())) {
                        keys = keys.takeRight(99)
                        keys :+ x.key()
                        this.synchronized{
                            msgList.add(x.value())
                        }
                    }
                })
            }
        } catch {
            case _: InterruptException =>
            case e: Exception => e.printStackTrace()
        }
    }

    def hasNext: Boolean ={
        if(msgList.isEmpty) path.delete()
        !msgList.isEmpty
    }

    def next: V = {
        saveMsg()
        msgList.pollFirst()
    }

    def saveMsg(): Unit ={
        path.delete()
        path.createNewFile()
        val write = new FileWriter(path)
        this.synchronized{
            msgList.asScala.foreach(x => write.write(x.toString + "\n"))
        }
        write.flush()
        write.close()
    }

    def close(): Unit ={
        consumer.close()
    }
}
