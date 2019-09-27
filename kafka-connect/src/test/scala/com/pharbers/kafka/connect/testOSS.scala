package com.pharbers.kafka.connect

import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.Charset

import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.OSSObject

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/09/25 15:36
  * @note 一些值得注意的地方
  */
object testOSS extends App{
    val client = new OSSClientBuilder().build("oss-cn-beijing.aliyuncs.com", "LTAIEoXgk4DOHDGi", "x75sK6191dPGiu9wBMtKE6YcBBh8EI")
    val `object` = client.getObject("pharbers-resources", "patient_info_100.csv")
    val stream = `object`.getObjectContent
    val bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")))
    while (bufferedReader.ready()){
        println(bufferedReader.readLine().split(",").mkString(","))
    }
}
