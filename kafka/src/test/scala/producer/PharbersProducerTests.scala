package producer

import java.util.concurrent.TimeUnit

import com.pharbers.kafka.producer.PharbersKafkaProducer
import org.scalatest.FunSuite

/**
  * @ ProjectName micro-service-libs.producer.PharbersProducerTests
  * @ author jeorch
  * @ date 19-6-11
  * @ Description: TODO
  */
class PharbersProducerTests extends FunSuite {

    test("PharbersKafkaProducer") {
        val fu = PharbersKafkaProducer.apply.produce("test", "key", "aha110".getBytes)
        println(fu.get(10, TimeUnit.SECONDS))
    }

}
