package com.pharbers.kafka.connect

import com.pharbers.kafka.connect.oss.model.ExcelTitle
import org.codehaus.jackson.map.ObjectMapper
import collection.JavaConverters._

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
    val list = List(new ExcelTitle("a", "b"), new ExcelTitle("a", "b"))
    val a = new ObjectMapper().writeValueAsString(list.asJava)
    println(a)
}
