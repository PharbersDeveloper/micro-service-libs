package producer

import java.io.File
import java.util.concurrent.TimeUnit

import com.pharbers.kafka.producer.PharbersKafkaProducer
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import com.pharbers.kafka.schema.RecordDemo
import org.scalatest.FunSuite
import scalaj.http.Http


/**
  * @ ProjectName micro-service-libs.producer.PharbersProducerTests
  * @ author jeorch
  * @ date 19-6-11
  * @ Description: TODO
  */
class PharbersProducerTests extends FunSuite {

    test("PharbersKafkaProducer") {
        val fu = PharbersKafkaProducer.apply.produce("test", "key", "aha1024".getBytes)
        println(fu.get(10, TimeUnit.SECONDS))
    }

    test("PharbersKafkaProducer with avro use GenericRecord") {

        val sche: Schema = Schema.parse(new File("pharbers_config/RecordDemo.avsc"))
        val gr: GenericRecord = new GenericData.Record(sche)

        val pkp = new PharbersKafkaProducer[String, GenericRecord]
        gr.put("id", "002")
        gr.put("name", "koko")

        val fu = pkp.produce("test6", "key", gr)
        println(fu.get(10, TimeUnit.SECONDS))
    }

    test("PharbersKafkaProducer with avro use SpecificRecord") {
        val pkp = new PharbersKafkaProducer[String, RecordDemo]
        val rd = new RecordDemo("005", "WOLO")
        val fu = pkp.produce("test6", "key", rd)
        println(fu.get(10, TimeUnit.SECONDS))
    }

    test("Create Schema") {
        //{
        //  "schema": "{\"type\":\"record\",\"name\":\"RecordDemo\",\"namespace\":\"com.pharbers.kafka.schema\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"}]}"
        //}
        val createSchemaResult = Http("http://59.110.31.50:8081/subjects/test6-value/versions")
//            .postData("{\"schema\": \"{\\\"type\\\": \\\"string\\\"}\"}")
            .postData("{\"schema\": \"{\\\"type\\\":\\\"record\\\",\\\"name\\\":\\\"RecordDemo\\\",\\\"namespace\\\":\\\"com.pharbers.kafka.schema\\\",\\\"fields\\\":[{\\\"name\\\":\\\"id\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"name\\\",\\\"type\\\":\\\"string\\\"}]}\"}")
            .header("Content-Type", "application/vnd.schemaregistry.v1+json")
            .asString
        println(createSchemaResult)
    }

}
