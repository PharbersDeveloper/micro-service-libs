package com.pharbers.util.log

trait phLogTrait extends phLogBase {

    def phLog(msg: Any, level: String = "DEBUG"): Unit = {
        if (SET_LEVEL().isEmpty) // 数据库未设置Log等级
            println(s" ==default=> " + msg)
        else if (SET_LEVEL().below(level)) // 设置等级
            println(s" ==$level=> " + msg)
        else
            Unit
    }

    def phDebugLog: Any => Unit = phLog(_)

    def phInfoLog: Any => Unit = phLog(_, "INFO")

    def phWarnLog: Any => Unit = phLog(_, "WARN")

    def phErrorLog: Any => Unit = phLog(_, "ERROR")
}

object phLogTrait extends phLogTrait