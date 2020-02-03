package com.pharbers.kafka.connect

import com.pharbers.kafka.connect.oss.model.{CellData, ExcelTitle}
import com.pharbers.kafka.schema.OssTask
import org.apache.avro.specific.SpecificRecordBase
import org.codehaus.jackson.map.ObjectMapper

import collection.JavaConverters._
import scala.reflect.ClassTag

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/10/30 16:11
  * @note 一些值得注意的地方
  */
object testJaon extends App {
    val list = List(new CellData("a", "b"), new CellData("a", "b"))
    val a = new ObjectMapper().writeValueAsString(list.asJava)
    println(a)
    println(new ObjectMapper().writeValueAsString(a))
}

object testClass extends App{

    class a[T](classTag: Class[T]) {
        val ins: T = classTag.newInstance()
//        val ins: T = Class.forName(implicitly[ClassTag[T]].runtimeClass.getCanonicalName).newInstance().asInstanceOf[T]
    }
    println(new a(classOf[OssTask]).ins)
}


