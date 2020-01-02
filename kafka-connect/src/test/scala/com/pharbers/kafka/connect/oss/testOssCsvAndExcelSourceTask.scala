package com.pharbers.kafka.connect.oss

import java.util
import java.util.concurrent.LinkedBlockingQueue
import collection.JavaConverters._
import com.pharbers.kafka.connect.oss.concurrent.RowData
import com.pharbers.kafka.schema.OssTask
import com.pharbers.util.log.PhLogable
import org.scalatest.FunSuite

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2020/01/02 13:31
  * @note 一些值得注意的地方
  */
class testOssCsvAndExcelSourceTask extends FunSuite with PhLogable{
    test("test poll"){
        val task = new OssTask("", "jobId", "traceId", "ossKey", new util.ArrayList[Integer](), "xlsx", "testFileName", "",
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence](),
            new util.ArrayList[CharSequence]())
        val ossCsvAndExcelSourceTask = new OssCsvAndExcelSourceTask()
        val plate = new LinkedBlockingQueue[RowData]()
        ossCsvAndExcelSourceTask.setPlate(plate)
        ossCsvAndExcelSourceTask.setBatchSize(500)
        ossCsvAndExcelSourceTask.setRecordBuilder(new ossCsvAndExcelSourceTask.RecordBuilder("test"))
        plate.put(new RowData("SandBox-Schema", Array("title1", "title2", "title3"), new util.HashMap(), "test", "testTraceId"))
        plate.put(new RowData("SandBox", Array("1", "2", "3"), new util.HashMap(), "test", "testTraceId"))
        plate.put(new RowData("SandBox-Length", Array("1"), new util.HashMap(), "test", "testTraceId"))
        plate.put(new RowData("SandBox-Labels", Array("sheetName"), new util.HashMap[String, Object](){{put("task", task)}}, "test", "testTraceId"))
        val list = ossCsvAndExcelSourceTask.poll()
        assert(list.size() == 4)
        list.asScala.foreach(x => logger.debug(x.value().toString))
    }
}
