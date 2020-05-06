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
  * @since 2020/01/02 10:43
  * @note 一些值得注意的地方
  */
class TestExcelReaderV2 extends FunSuite with BeforeAndAfterAll with PhLogable{
    val task = new OssTask("", "jobId", "traceId", "ossKey", "xlsx", "test", "", "", 0L,
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence]())
    val reader = new ExcelReaderV2("test", task)
    val path = "src/test/resources/1575555558843.xlsx"
    val stream = new FileInputStream(new File(path))
    test("test excel reader"){
        val plate = new LinkedBlockingQueue[RowData]()
        reader.init(stream, "")
        reader.read(plate)
        plate.asScala
                .groupBy(x => x.getJobId)
                .foreach(x => {
                    val rows = x._2.filter(data => data.getType == "SandBox")
                    val count = rows.size
                    val length = x._2.filter(data => data.getType == "SandBox-Length").head.getRow.head
                    logger.debug(s"jobId: ${x._1}, count: $count, length: $length")
                    x._1 match {
                        case "test0" => assert(count == 14 && length == "14")
                        case "test1" => assert(count == 879 && length == "879")
                        case "test2" => assert(count == 20516 && length == "20516")
                        case "test3" => assert(count == 52 && length == "52")
                    }
                    val check = getCheck(x._1)
                    val rowIterator = rows.iterator
                    while (rowIterator.hasNext){
                        val row = rowIterator.next().getRow
                        val checkRow = check.nextLine().replace("\uFEFF", "").replaceAll("\"", "")
                        println(row.mkString(","))
                        println(checkRow)
                        assert(row.mkString(",").equals(checkRow))
                    }
                })
    }

    def getCheck(jobId: String): Scanner = {
        val path = jobId match {
            case "test0" => "src/test/resources/1-3月未到医院名单及说明.csv"
            case "test1" => "src/test/resources/需替换及补充数据.csv"
            case "test2" => "src/test/resources/施贵宝1903.csv"
            case "test3" => "src/test/resources/药品信息更新列表.csv"
        }
        new Scanner(new File(path))
    }

    override def afterAll(): Unit = {
        reader.close()
        stream.close()
    }
}
