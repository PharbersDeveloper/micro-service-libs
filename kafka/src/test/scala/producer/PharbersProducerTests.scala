package producer

import java.io.File
import java.util
import java.util.concurrent.TimeUnit

import com.pharbers.kafka.producer.PharbersKafkaProducer
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import com.pharbers.kafka.schema.{OssTask, PhErrorMsg, RecordDemo}
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
        val jobId = "202001061133"
        val traceId = "202001061133"
        val ossKey = "07a1d883-def2-4301-8f8c-28527335939b/1578882666129"
        val fileType = "xlsx"

        val sche: Schema = new Schema.Parser().parse(new File("src/main/avro/OssTask.avsc"))
//        val gr: OssTask = new OssTask("", jobId, traceId, ossKey, new util.ArrayList[Integer](), fileType, "test", "",
//            new util.ArrayList[CharSequence](),
//            new util.ArrayList[CharSequence](),
//            new util.ArrayList[CharSequence](),
//            new util.ArrayList[CharSequence](),
//            new util.ArrayList[CharSequence](),
//            new util.ArrayList[CharSequence]())

        val gr: OssTask = new OssTask("5df75f48ea4ea33708c05af2", jobId, traceId, ossKey, fileType, "Product standardization master data-HBV.xlsx", "",
            "", 0L,
            new util.ArrayList[CharSequence](){{add("底层数据")}},
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](){{add("Pharbers"); add("PROD")}})

        val pkp = new PharbersKafkaProducer[String, OssTask]
        gr.put("jobId", jobId)
        gr.put("traceId", traceId)
        gr.put("ossKey", ossKey)
        gr.put("fileType", fileType)
        val fu = pkp.produce("oss_task_submit", jobId, gr)
        println(fu.get(10, TimeUnit.SECONDS))
    }

    test("PharbersKafkaProducer with avro use SpecificRecord") {
        val pkp = new PharbersKafkaProducer[String, PhErrorMsg]
        val rd = new PhErrorMsg("005", "WOLO", "", "test", "testException", "test" ,"")
        val fu = pkp.produce("pharbers_error", "key", rd)
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
