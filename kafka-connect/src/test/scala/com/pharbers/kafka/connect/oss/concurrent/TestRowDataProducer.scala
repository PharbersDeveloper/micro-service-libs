package com.pharbers.kafka.connect.oss.concurrent

import java.util
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder
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
        "accessKeyId" -> "LTAI4Fuc5oo46peAcc3LmHb3",
        "accessKeySecret" -> "aJRr3DP4nXCFDR3KGRICpIhq5bHfTm",
        "bucketName" -> "pharbers-sandbox"
    )

    val threadNum = 8

    test("test close Stream"){
        val product = new RowDataProducer(null, null, config.asJava)
        val task = new OssTask("test", "jobId", "traceId", "5211b69b-d568-43ab-8ce4-968c7cf5a04e/1575882092028",
            "fileType", "test", "", "", 0L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())
        val ossObject = product.getOSSObject(task)
//        val read = new BufferedReader(new InputStreamReader(stream))
//        println(read.readLine())
//        read.close()
        println("close stream")
        ossObject.forcedClose()
        println("close read")
//        read.close()
        println("end")
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
        for(i <- 1 to threadNum){
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
}
