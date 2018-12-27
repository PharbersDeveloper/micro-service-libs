package com.pharbers.util.log

import java.io.FileNotFoundException

import com.pharbers.driver.PhRedisDriver

trait phLogBase {
    val LEVEL_KEY = "PhLogLevel"
    val rd: PhRedisDriver = new PhRedisDriver()

    implicit val levelTab: Map[String, Int] = Map(
        "DEBUG" -> 0,
        "INFO" -> 1,
        "WARN" -> 2,
        "ERROR" -> 3
    )

    implicit class LevelCompare(a: String)(implicit levelTab: Map[String, Int]) {
        def below(b: String): Boolean =
            levelTab.getOrElse(a.toUpperCase, 0) <= levelTab.getOrElse(b.toUpperCase, 0)
    }

    def SET_LEVEL(): String = try {
        rd.getString(LEVEL_KEY) match {
            case str: String => str
            case null => ""
        }
    } catch {
        case _: Exception => ""
    }
}
