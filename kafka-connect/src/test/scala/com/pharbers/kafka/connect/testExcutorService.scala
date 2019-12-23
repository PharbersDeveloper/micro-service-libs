package com.pharbers.kafka.connect

import java.io.{BufferedReader, InputStreamReader}
import java.net.InetAddress
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

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

    val executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue[Runnable]())
    executorService.execute(new testRun())
    Thread.sleep(1000)
    executorService.shutdownNow()
    executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)
    println("shutdown*************")
}

class testRun() extends Runnable{
    override def run(): Unit = {
        while(!Thread.currentThread().isInterrupted){
            Thread.sleep(100)
            println("run")
        }
        println("end*************")
    }
}