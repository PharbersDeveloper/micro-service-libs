package com.pharbers.util.log

import org.apache.logging.log4j.{LogManager, Logger, ThreadContext}
import scala.collection.JavaConverters._

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/09/09 11:02
  * @note 一些值得注意的地方
  */
trait PhLogable{
    protected val logger: Logger = LogManager.getLogger(this)
}

object PhLogable {
    def useJobId[B](userId: String, traceId: String, jobId: String)(f: Unit => B): B ={
        try {
            ThreadContext.putAll(Map("USERID" -> userId, "TRACEID" -> traceId, "JOBID" -> jobId).asJava)
            f()
        }
        finally {
            ThreadContext.clearAll()
        }
    }
}
