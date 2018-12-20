package com.pharbers.util.log

object main extends App with phLogTrait {
    phLog("common")
    phDebugLog("debug")
    phInfoLog("info")
    phWarnLog("warn")
    phErrorLog("error")
}
