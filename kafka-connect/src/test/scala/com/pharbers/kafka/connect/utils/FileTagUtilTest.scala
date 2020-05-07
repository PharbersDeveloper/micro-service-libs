package com.pharbers.kafka.connect.utils

import collection.JavaConverters._
import org.scalatest.FunSuite

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2020/05/07 11:11
  * @note 一些值得注意的地方
  */
class FileTagUtilTest extends FunSuite{
    test("test createTag"){
        val res = FileTagUtil.createTag(Array("a", "b", "c"))
        val separator = 31.toChar.toString
        println(res)
        assert(res == s"phf${separator}a${separator}b${separator}c")
    }
}
