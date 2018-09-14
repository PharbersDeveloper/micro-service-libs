package com.pharbers.util.log

trait phLogTrait {
    def phLog(msg: Any) = println(" ===> " + msg)
}

object phLogTrait extends phLogTrait