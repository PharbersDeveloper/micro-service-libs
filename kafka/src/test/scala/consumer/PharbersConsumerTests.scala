package consumer

import com.pharbers.kafka.consumer.PharbersKafkaConsumer
import com.pharbers.kafka.schema.RecordDemo
import io.confluent.ksql.avro_schemas.KsqlDataSourceSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.scalatest.FunSuite

/**
  * @ ProjectName micro-service-libs.consumer.PharbersConsumerTests
  * @ author jeorch
  * @ date 19-6-11
  * @ Description: TODO
  */
class PharbersConsumerTests extends FunSuite {

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
            println("===myProcess>>>" + record.key() + ":" + record.value().getCORPNAME.toString)
        }
        val pkc = new PharbersKafkaConsumer[String, KsqlDataSourceSchema](List("DCS"), 1000, Int.MaxValue, testProcess)
        val consumer = pkc.getConsumer
        consumer.partitionsFor("DCS")
        consumer.endOffsets(List(new TopicPartition("DCS", 1)).asJava)
        val t = new Thread(pkc)
        t.start()
        Thread.sleep(500000)
        pkc.close()
        println("DOWN")
    }

    def myProcess[K, V](record: ConsumerRecord[K, V]): Unit = {
        println("===myProcess>>>" + record.key() + ":" + record.value())
    }

}
