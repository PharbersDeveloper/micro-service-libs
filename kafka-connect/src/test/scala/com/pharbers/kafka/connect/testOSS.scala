package com.pharbers.kafka.connect

import java.io.{BufferedReader, InputStreamReader}
import java.net.InetAddress
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.Date

import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.OSSObject

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/09/25 15:36
  * @note 一些值得注意的地方
  */
object Solution extends App{
    val fomatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z")
    val a = fomatter1.parse("2019-10-11T03:12:36.809 UTC")
    val time = new Date()
    println(time)
    println(InetAddress.getLocalHost.getHostName)

    def findWords(board: Array[Array[Char]], words: Array[String]): List[String] = {
        ???
    }
}
