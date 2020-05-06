package com.pharbers.kafka.connect

import java.io._
import java.util
import java.util.concurrent.{ConcurrentHashMap, CopyOnWriteArrayList}
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.OSSObject
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.monitorjbl.xlsx.StreamingReader
import collection.JavaConverters._
import com.pharbers.kafka.connect.oss.reader.ExcelReader
import com.pharbers.kafka.schema.OssTask
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.SourceRecord
import org.bson.BsonDocument
import org.mozilla.universalchardet.{ReaderFactory, UniversalDetector}

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/11/26 16:40
  * @note 一些值得注意的地方
  */
object TestExcelReader extends App with Runnable{
    val path = "C:\\Users\\EDZ\\Desktop\\1575555558843.xlsx"
    val threadNum = 8
    val reader = init(path)
    val list = new CopyOnWriteArrayList[SourceRecord]()
    (0 until threadNum).foreach(_ => new Thread(this).start())
    while (!reader.isEnd){
        Thread.sleep(1000)
    }

    list.asScala.map(x => x.value().asInstanceOf[Struct])
            .groupBy(x => x.get("jobId").asInstanceOf[String])
            .foreach(x => {
                println(s"jobId: ${x._1}")
                println(x._2.filter(data => data.get("type") != "SandBox").mkString(","))
                println(x._2.count(data => data.get("type") == "SandBox"))
                println(x._2.find(data => data.get("type") == "SandBox").get)
            })

    def run(): Unit ={
        while (!reader.isEnd){
            list.addAll(reader.read())
        }
    }

    def init(path: String): ExcelReader ={
        val task = new OssTask("", "jobId", "traceId", "ossKey", "xlsx", "test", "", "", 0L,
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())
        val stream = new FileInputStream(new File(path))
        val excelReader = new ExcelReader("test", 500)
        excelReader.init(stream, task, new util.HashMap[String, Object](0))
        excelReader
    }
}

object TestExcel extends App{
    val path = "src/test/resources/Hospital Data for Zytiga Market 201801-201908.xlsx"
    val stream = new FileInputStream(new File(path))
    val reader = StreamingReader.builder().open(stream)
    val cells = reader.getSheetAt(0).rowIterator().next().cellIterator()
    while (cells.hasNext){
        val cell = cells.next()
        cell.getNumericCellValue
        cell.getCellType.name() match {
            case "NUMERIC" => println(cell.getNumericCellValue)
            case _ => println(cell.getStringCellValue)
        }

    }

    reader.close()
}

object TestCsvFormat extends App{
    val file = new File("C:\\Users\\EDZ\\Desktop\\Product_matching_table_packid_v2.csv")
    val stream = new FileInputStream(file)
    val array = new Array[Byte](4096)
    stream.read(array)
    val detector = new UniversalDetector()
    detector.handleData(array, 0, 4096)
    detector.dataEnd()
    val encoding = detector.getDetectedCharset
    detector.reset()
}

object getCsvHeard extends App{
    val client = new OSSClientBuilder().build("oss-cn-beijing.aliyuncs.com", "LTAI4Fuc5oo46peAcc3LmHb3", "aJRr3DP4nXCFDR3KGRICpIhq5bHfTm")
    val mongoClient = MongoClients.create("mongodb://123.56.179.133:5555")
    val database = mongoClient.getDatabase("pharbers-sandbox-max-result")
    val files = database.getCollection("files", classOf[BsonDocument])
    val iter = files.find(Filters.eq("extension", "csv")).iterator()
//    val iter = files.find(Filters.eq("fileName", "信立泰完整权限.csv")).iterator()
    while (iter.hasNext){
        val url = iter.next().getString("url").getValue
        val obj = client.getObject("pharbers-sandbox", url)
        val reader = new BufferedReader(new InputStreamReader(obj.getObjectContent))
//        reader.readLine()
        val s = reader.readLine()
        val length = (s.split(",").length :: s.split(31.toChar.toString).length :: s.split("#").length :: Nil).max
        println(s"length:$length")
        if(length > 24) println(url)
        println(s)
        obj.forcedClose()
    }
}

object getCSV extends App{
    val client = new OSSClientBuilder().build("oss-cn-beijing.aliyuncs.com", "LTAI4Fuc5oo46peAcc3LmHb3", "aJRr3DP4nXCFDR3KGRICpIhq5bHfTm")
    val obj = client.getObject("pharbers-sandbox", "48750a49-232a-4039-b973-cd6ece31f6af/1575959605312")
    val file = new File("xinlitai.csv")
    file.createNewFile()
    val fileWrite = new FileWriter(file)
    println(obj.getObjectMetadata.getContentLength)
    val reader = new BufferedReader(new InputStreamReader(obj.getObjectContent, "UTF-16"))
    var s = reader.readLine()
    while (s != null){
        s = s.split("\t").mkString(",")
        fileWrite.write(s + "\n")
        Thread.sleep(1)
        s = reader.readLine()
    }
}

object getExcel extends App{
    val client = new OSSClientBuilder().build("oss-cn-beijing.aliyuncs.com", "LTAI4Fuc5oo46peAcc3LmHb3", "aJRr3DP4nXCFDR3KGRICpIhq5bHfTm")
    val obj = client.getObject("pharbers-sandbox", "48750a49-232a-4039-b973-cd6ece31f6af/1575959605312")
    val file = new File("excel")
    file.createNewFile()
    val fileWrite = new FileWriter(file)
    println(obj.getObjectMetadata.getContentLength)
    val reader = new BufferedReader(new InputStreamReader(obj.getObjectContent, "UTF-16"))
    var s = reader.readLine()
    while (s != null){
        s = s.split("\t").mkString(",")
        fileWrite.write(s + "\n")
        Thread.sleep(1)
        s = reader.readLine()
    }
}

