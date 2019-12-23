package consumer

import java.time.Duration

import com.pharbers.kafka.consumer.PharbersKafkaConsumer
import com.pharbers.kafka.schema.{OssTask, RecordDemo}
import com.pharbers.util.log.PhLogable
import io.confluent.ksql.avro_schemas.KsqlDataSourceSchema
import kafka.cluster.Partition
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.scalatest.FunSuite

/**
  * @ ProjectName micro-service-libs.consumer.PharbersConsumerTests
  * @ author jeorch
  * @ date 19-6-11
  * @ Description: TODO
  */
class PharbersConsumerTests extends FunSuite with PhLogable{

    test("PharbersKafkaConsumer") {
//        val pkc = new PharbersKafkaConsumer[String, Array[Byte]](kafka_config_obj.topics.toList, 1000, Int.MaxValue)
        val pkc = new PharbersKafkaConsumer[String, String](List("test5"), 1000, Int.MaxValue, myProcess)
        val t = new Thread(pkc)
        t.start()
        Thread.sleep(50000)
        t.stop()
        println("DOWN")
    }

    test("PharbersKafkaConsumer with avro") {
        import scala.collection.JavaConverters._
        def testProcess[K, V](record: ConsumerRecord[String, KsqlDataSourceSchema]): Unit = {
            println("===myProcess>>>" + record.key() + ":" + record.value().getCORPNAME.toString + ":" + record.offset())
        }
        val pkc = new PharbersKafkaConsumer[String, KsqlDataSourceSchema](List("DCS1"), 1000, Int.MaxValue, testProcess)
        val consumer = pkc.getConsumer
        consumer.endOffsets(List(new TopicPartition("DCS1", 1)).asJava)
        consumer.beginningOffsets(List(new TopicPartition("DCS1", 1)).asJava)
        val t = new Thread(pkc)
        t.start()
        Thread.sleep(500000)
        pkc.close()
        println("DOWN")
    }

    def myProcess[K, V](record: ConsumerRecord[K, V]): Unit = {
        println("===myProcess>>>" + record.key() + ":" + record.value())
    }

    test("oss task"){
        import scala.collection.JavaConverters._
        val consumer = new PharbersKafkaConsumer[String, OssTask](List("oss_task")).getConsumer
        consumer.subscribe(List("oss_task").asJava)
        while (true){
            val end = consumer.endOffsets(List(new TopicPartition("oss_source_1", 0)).asJava, Duration.ofSeconds(10))
            println(end)
//            val ossTasks = consumer.poll(Duration.ofSeconds(1)).iterator()
//            val ossKey = ossTasks.next().value().get("ossKey").toString
//            println(ossKey)
        }
    }

}
