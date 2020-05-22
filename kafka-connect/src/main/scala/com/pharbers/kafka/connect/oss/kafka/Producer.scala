package com.pharbers.kafka.connect.oss.kafka

import java.util.concurrent.{ExecutionException, Future, TimeUnit, TimeoutException}

import com.pharbers.kafka.connect.oss.model.BloodMsg
import com.pharbers.kafka.connect.utils.JsonUtil
import com.pharbers.kafka.producer.PharbersKafkaProducer
import com.pharbers.kafka.schema.{EventMsg, PhErrorMsg}
import org.apache.kafka.clients.producer.RecordMetadata

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/11/26 17:20
  * @note 一些值得注意的地方
  */
private[kafka] class Producer {

    def pushErr(msg: PhErrorMsg): Unit = {
        val pkp = new PharbersKafkaProducer[String, PhErrorMsg]
        val topic = "pharbers_error"
        val fu = pkp.produce(topic, msg.getErrorCode.toString, msg)
        try
            println(fu.get(10, TimeUnit.SECONDS))
        catch {
            case e@(_: InterruptedException | _: ExecutionException | _: TimeoutException) =>
                e.printStackTrace()
        }
        pkp.producer.close()
    }

    def pushStatus(msg: BloodMsg, traceId: String): Unit ={
        val pkp = new PharbersKafkaProducer[String, EventMsg]
        val topic = "oss_msg"
        val event = new EventMsg(msg.getJobId, traceId, "SandBoxDataSet", JsonUtil.MAPPER.writeValueAsString(msg))
        val fu = pkp.produce(topic, "", event)
        try
            println(fu.get(10, TimeUnit.SECONDS))
        catch {
            case e@(_: InterruptedException | _: ExecutionException | _: TimeoutException) =>
                e.printStackTrace()
        }
        pkp.producer.close()
    }
}

object Producer {
    private val ins = new Producer
    def getIns: Producer = ins
}

