package com.pharbers.kafka.connect.oss.readerV2

import java.io.{File, FileInputStream}
import java.util
import java.util.Scanner
import java.util.concurrent.LinkedBlockingQueue

import collection.JavaConverters._
import com.pharbers.kafka.connect.oss.concurrent.RowData
import com.pharbers.kafka.schema.OssTask
import com.pharbers.util.log.PhLogable
import org.scalatest.{BeforeAndAfterAll, FunSuite}

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2020/01/02 14:46
  * @note 一些值得注意的地方
  */
class TestCsvReaderV2 extends FunSuite with BeforeAndAfterAll with PhLogable {
    val task = new OssTask("", "jobId", "traceId", "ossKey", "xlsx", "test", "", "", 0L,
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence]())
    val reader = new CsvReaderV2("test", task)
    val path = "src/test/resources/1575555558843.csv"
    val stream = new FileInputStream(new File(path))

    test("test csv reader"){
        val plate = new LinkedBlockingQueue[RowData]()
        reader.init(stream, "utf-8")
        reader.read(plate)
        plate.asScala
                .groupBy(x => x.getJobId)
                .foreach(x => {
                    val rows = x._2.filter(data => data.getType == "SandBox")
                    val count = rows.size
                    val length = x._2.filter(data => data.getType == "SandBox-Length").head.getRow.head
                    println(s"jobId: ${x._1}, count: $count, length: $length")
                    x._1 match {
                        case "test0" => assert(count == 14 && length == "14")
                    }
                    val check = getCheck(x._1)
                    val rowIterator = rows.iterator
                    while (rowIterator.hasNext){
                        val row = rowIterator.next().getRow
                        val checkRow = check.nextLine().replace("\uFEFF", "").replaceAll("\"", "")
                        println(row.mkString(","))
                        println(checkRow)
                        assert(row.mkString(",").equals(checkRow.split(",").mkString(",")))
                    }
                })
    }

    def getCheck(jobId: String): Scanner = {
        val path = jobId match {
            case "test0" => "src/test/resources/1-3月未到医院名单及说明.csv"
        }
        new Scanner(new File(path))
    }

    override def afterAll(): Unit = {
        reader.close()
        stream.close()
    }
}
