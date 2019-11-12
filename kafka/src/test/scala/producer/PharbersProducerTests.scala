package producer

import java.io.File
import java.util
import java.util.concurrent.TimeUnit

import com.pharbers.kafka.producer.PharbersKafkaProducer
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import com.pharbers.kafka.schema.{OssTask, RecordDemo}
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
        val jobId = "201911121509"
        val traceId = "201911121509"
        val ossKey = "test1.xlsx"
        val fileType = "xlsx"

        val sche: Schema = new Schema.Parser().parse(new File("src/main/avro/OssTask.avsc"))
        val gr: OssTask = new OssTask(jobId, traceId, ossKey, fileType, "test", "",
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())

        val pkp = new PharbersKafkaProducer[String, OssTask]
        gr.put("jobId", jobId)
        gr.put("traceId", traceId)
        gr.put("ossKey", ossKey)
        gr.put("fileType", fileType)
        val fu = pkp.produce("oss_task_submit", jobId, gr)
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
