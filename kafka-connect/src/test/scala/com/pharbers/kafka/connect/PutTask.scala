package com.pharbers.kafka.connect

import java.util
import java.util.UUID
import java.util.concurrent.TimeUnit

import com.aliyun.oss.OSSClientBuilder
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.pharbers.kafka.producer.PharbersKafkaProducer
import com.pharbers.kafka.schema.OssTask
import org.bson.{BsonDocument, Document}
import collection.JavaConverters._

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2020/01/17 10:08
  * @note 一些值得注意的地方
  */
object PutTask extends App {
    while (true){
        push
        Thread.sleep(1000 * 60 * 30)
    }

    def push: Unit ={
        val removeCsv = List("5211b69b-d568-43ab-8ce4-968c7cf5a04e/1575882092028", "77d7422e-96b3-4555-9105-7d9d03ce8b8c/1575882711010")
        val client = new OSSClientBuilder().build("oss-cn-beijing.aliyuncs.com", "LTAI4Fuc5oo46peAcc3LmHb3", "aJRr3DP4nXCFDR3KGRICpIhq5bHfTm")
        val mongoClient = MongoClients.create("mongodb://123.56.179.133:5555")
        val database = mongoClient.getDatabase("pharbers-sandbox-merge")
        val files = database.getCollection("files", classOf[BsonDocument])
        val assets = database.getCollection("assets", classOf[BsonDocument])
        val iter = assets.find(Document.parse("{'providers':{$in:['倍特']}}")).iterator()
        while (iter.hasNext){
            val doc = iter.next()
            if(doc.getString("name").getValue.takeRight(3) != "xls") {
                val count = 1
                val file = files.find(Filters.eq(doc.getObjectId("file").getValue)).first()
                //        if(file.getString("extension").getValue == "csv" && !removeCsv.contains(file.getString("url").getValue)){
                val jobId = UUID.randomUUID().toString
                val task = getTask(file, doc, jobId)
                val pkp = new PharbersKafkaProducer[String, OssTask]
                val fu = pkp.produce("oss_task_submit", jobId, task)
                val res = fu.get(10, TimeUnit.SECONDS)
                println(res)
                //        }
            }
        }
    }

    def getTask(file: BsonDocument, assset: BsonDocument, jobId: String): OssTask ={
        val traceId = "test beite"
        val assetId = assset.getObjectId("_id").getValue.toString
        val ossKey = file.getString("url").getValue
        val fileType = file.getString("extension").getValue
        val fileName = assset.getString("name").getValue
        val owner = assset.getString("owner").getValue
        val createTime = assset.getDouble("createTime").getValue.toLong
        val labels = assset.getArray("labels").getValues.asScala.map(x => x.asString().getValue).asJava
        val dataCover = assset.getArray("dataCover").getValues.asScala.map(x => x.asString().getValue).asJava
        val geoCover = assset.getArray("geoCover").getValues.asScala.map(x => x.asString().getValue).asJava
        val markets = assset.getArray("markets").getValues.asScala.map(x => x.asString().getValue).asJava
        val molecules = assset.getArray("molecules").getValues.asScala.map(x => x.asString().getValue).asJava
        val providers = assset.getArray("providers").getValues.asScala.map(x => x.asString().getValue).asJava
        new OssTask(assetId, jobId, traceId, ossKey, fileType, fileName, "", owner, createTime,
            new util.ArrayList[CharSequence](labels),
            new util.ArrayList[CharSequence](dataCover),
            new util.ArrayList[CharSequence](geoCover),
            new util.ArrayList[CharSequence](markets),
            new util.ArrayList[CharSequence](molecules),
            new util.ArrayList[CharSequence](providers))
    }
}

object PushTest extends App{
    val pkp = new PharbersKafkaProducer[String, OssTask]
    var count = 0
    while (true){
        val task = new OssTask("5e79e004d589564646ace709",
            "1111111111111112",
            "1111111111111112",
            "5c2e3756-2363-4d28-97e2-cdc5b20c82ad/1574249019807",
            "xlsx",
            "2017年1月辉瑞头孢孟多产品数据.xlsx", "",
            "owner",
            10000L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](List("Pfizer", "CPA&GYC").asJava))
        val fu = pkp.produce("oss_task_submit", UUID.randomUUID().toString, task)
        val res = fu.get(10, TimeUnit.SECONDS)
        println(res)
        count += 1
        println(count)
        Thread.sleep(1000 * 5)
    }

}
