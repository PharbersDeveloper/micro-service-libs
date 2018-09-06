package com.pharbers.driver.util

/**
  * Created by jeorch on 17-12-13.
  */
trait PhRedisTrait {
    def addString(key: Any, value: String)
    def addListLeft(key: Any, values: Any*)
    def addListRight(key: Any, values: Any*)
    def addMap(key: Any, value: Map[Any, Any])
    def addMap(key: Any, mapK: Any, mapV: Any)
    def addSet(key: Any, value: Set[Any])
    def addSet(key: Any, values: Any*)
    def delete(key: Any, keys: Any*) : Long
    def getString(key: Any) : String
    def getListAllValue(key: Any) : List[String]
    def getListLeftValue(key: Any) : String
    def getListRightValue(key: Any) : String
    def getListSize(key: Any) : Long
    def getMapAllValue(key: Any) : Map[String, String]
    def getMapSize(key: Any) : Long
    def getMapValue(key: Any, mapK: Any) : String
    def getSetAllValue(key: Any) : Set[String]
    def getSetLastValue(key: Any) : String
    def getSetSize(key: Any) : Long
    def expire(key: Any, ttl: Int)
    def exsits(key: Any) : Boolean
    def flush
}
