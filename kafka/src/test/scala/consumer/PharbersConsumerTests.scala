package consumer

import com.pharbers.kafka.consumer.PharbersKafkaConsumer
import com.pharbers.kafka.schema.RecordDemo
import org.apache.kafka.clients.consumer.ConsumerRecord
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
        val pkc = new PharbersKafkaConsumer[String, RecordDemo](List("test6"), 1000, Int.MaxValue, myProcess)
        val t = new Thread(pkc)
        t.start()
        Thread.sleep(50000)
        t.stop()
        println("DOWN")
    }

    def myProcess[K, V](record: ConsumerRecord[K, V]): Unit = {
        println("===myProcess>>>" + record.key() + ":" + record.value())
    }

}
