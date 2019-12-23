package com.pharbers.util.log.plugin

import java.lang
import java.net.InetAddress

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.pattern.{ConverterKeys, LogEventPatternConverter, PatternConverter}


/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/09/10 10:32
  * @note 一些值得注意的地方
  */
@Plugin(name = "host", category = PatternConverter.CATEGORY)
@ConverterKeys(Array("host"))
class HostNamePatternConverter extends LogEventPatternConverter("host", "host") {
    override def format(event: LogEvent, toAppendTo: lang.StringBuilder): Unit = {
        event.getContextData
        toAppendTo.append(InetAddress.getLocalHost.getHostName)
    }
}

object HostNamePatternConverter {
    def newInstance(): HostNamePatternConverter = new HostNamePatternConverter
}



