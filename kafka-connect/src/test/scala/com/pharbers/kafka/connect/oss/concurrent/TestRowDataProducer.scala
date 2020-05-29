package com.pharbers.kafka.connect.oss.concurrent

import java.io._
import java.util
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder
import com.pharbers.kafka.connect.oss.readerV2.{CsvReaderV2, ExcelReaderForMaxDeliveryData, ExcelReaderV2}
import com.pharbers.kafka.schema.OssTask
import org.scalatest.FunSuite

import collection.JavaConverters._

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2020/01/09 18:03
  * @note 一些值得注意的地方
  */
class TestRowDataProducer extends FunSuite{
    val config: Map[String, String] = Map(
        "endpoint" -> "oss-cn-beijing.aliyuncs.com",
        "accessKeyId" -> sys.env("OSS_ACCESS_KEY_ID"),
        "accessKeySecret" -> sys.env("OSS_ACCESS_KEY_SECRET"),
        "bucketName" -> "pharbers-sandbox"
    )

    val threadNum = 8

    test("test close Stream"){
        val product = new RowDataProducer(null, null, config.asJava)
        val task = new OssTask("test", "jobId", "traceId", "29122-4ad8-4b10-a528-2be2c/1590202804983",
            "fileType", "test", "", "", 0L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())
        val ossObject = product.getOSSObject(task)
        val stream = ossObject.getObjectContent
        val file = new File("./down.xlsx")
        file.createNewFile()
        val fos = new FileOutputStream(file)
        val bytes = new Array[Byte](1024)
        var read = stream.read(bytes)
        while (read != -1) {
            fos.write(bytes, 0, read)
            read = stream.read(bytes)
        }
//        val read = new BufferedReader(new InputStreamReader(stream))
//        println(read.readLine())
//        read.close()
        println("close stream")
        ossObject.forcedClose()
        println("close read")
//        read.close()
        println("end")
        file.delete()
    }

    test("test csv format"){
        val plate = new LinkedBlockingQueue[RowData](1000)
        val product = new RowDataProducer(null, plate, config.asJava)
        val task = new OssTask("test", "jobId", "traceId", "48750a49-232a-4039-b973-cd6ece31f6af/1575959605312",
            "csv", "test", "", "", 0L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())
        for(_ <- 1 to threadNum){
            new Thread(new Runnable {
                override def run(): Unit = {
                    while (true){
                        println(plate.take().getRow.mkString(","))
                        Thread.sleep(10)
                    }
                }
            }).start()
        }
        product.readOss(task)
    }

    test("test stop"){
        val executorServices = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable])
        val kafkaConsumerBuffer = new ConsumerBuilder[String, OssTask]("test", classOf[OssTask])
        val producer = new RowDataProducer(kafkaConsumerBuffer, null, config.asJava)
        executorServices.execute(producer)
        Thread.sleep(10000)
        println("stop...")
        executorServices.shutdownNow
        executorServices.awaitTermination(5, TimeUnit.SECONDS)
        Thread.sleep(5000)
        assert(!producer.isRun)
    }

    test("test build ExcelReaderForMaxDeliveryData") {
        val plate = new LinkedBlockingQueue[RowData](1000)
        val product = new RowDataProducer(null, plate, config.asJava)
        val task = new OssTask("test", "jobId", "traceId", "48750a49-232a-4039-b973-cd6ece31f6af/1575959605312",
            "xlsx", "test", "", "", 0L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](){{add("delivery")}})
        val path = "src/test/resources/Amgen_MAX_data_仅201912.xlsx"
        val stream = new FileInputStream(new File(path))

        val reader = product.buildReader("xlsx", task, stream, "utf-8")
        assert(reader.isInstanceOf[ExcelReaderForMaxDeliveryData])
    }

    test("test build ExcelReaderV2") {
        val plate = new LinkedBlockingQueue[RowData](1000)
        val product = new RowDataProducer(null, plate, config.asJava)
        val task = new OssTask("test", "jobId", "traceId", "48750a49-232a-4039-b973-cd6ece31f6af/1575959605312",
            "xlsx", "test", "", "", 0L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())
        val path = "src/test/resources/Amgen_MAX_data_仅201912.xlsx"
        val stream = new FileInputStream(new File(path))

        val reader = product.buildReader("xlsx", task, stream, "utf-8")
        assert(reader.isInstanceOf[ExcelReaderV2])
    }

    test("test build CsvReaderV2") {
        val plate = new LinkedBlockingQueue[RowData](1000)
        val product = new RowDataProducer(null, plate, config.asJava)
        val task = new OssTask("test", "jobId", "traceId", "48750a49-232a-4039-b973-cd6ece31f6af/1575959605312",
            "csv", "test", "", "", 0L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())
        val path = "src/test/resources/1-3月未到医院名单及说明.csv"
        val stream = new FileInputStream(new File(path))

        val reader = product.buildReader("csv", task, stream, "utf-8")
        assert(reader.isInstanceOf[CsvReaderV2])
    }
}
