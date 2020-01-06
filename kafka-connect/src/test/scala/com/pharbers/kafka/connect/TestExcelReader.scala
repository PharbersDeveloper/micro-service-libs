package com.pharbers.kafka.connect

import java.io.{File, FileInputStream}
import java.util
import java.util.concurrent.CopyOnWriteArrayList

import com.monitorjbl.xlsx.StreamingReader

import collection.JavaConverters._
import com.pharbers.kafka.connect.oss.reader.ExcelReader
import com.pharbers.kafka.schema.OssTask
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.SourceRecord

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
        val task = new OssTask("", "jobId", "traceId", "ossKey", new util.ArrayList[Integer](), "xlsx", "test", "",
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
        cell.getCellType.name() match {
            case "NUMERIC" => println(cell.getNumericCellValue)
            case _ => println(cell.getStringCellValue)
        }

    }

    reader.close()
}