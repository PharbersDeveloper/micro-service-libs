package com.pharbers.cliTraits

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue

/**
  * Created by alfredyang on 01/06/2017.
  */
trait DBTrait {
    def insertObject(obj : DBObject, db_name : String, primary_key : String) : Unit
    def updateObject(obj : DBObject, db_name : String, primary_key : String) : Unit

    def queryObject(condition : DBObject, db_name : String)
                   (implicit t : DBObject => Map[String, JsValue]) : Option[Map[String, JsValue]]
    def queryMultipleObject(condition : DBObject, db_name : String, sort : String = "date", skip : Int = 0, take : Int = 20)
                           (implicit t : DBObject => Map[String, JsValue]) : List[Map[String, JsValue]]
    def queryMultipleWithOutSort(condition : DBObject, db_name : String, skip : Int = 0, take : Int = 20)
                                (implicit t : DBObject => Map[String, JsValue]) : List[Map[String, JsValue]]
    def queryCount(condition : DBObject, db_name : String)
                           (implicit t : DBObject => Map[String, JsValue]) : Option[Int]
    def querySum(condition : DBObject, db_name : String)
                (sum : (Map[String, JsValue], Map[String, JsValue]) => Map[String, JsValue])
                (acc: (DBObject) => Map[String, JsValue]) : Option[Map[String, JsValue]]

    def aggregate(condition : DBObject, db_name : String, group : DBObject)
                 (implicit t : DBObject => Map[String, JsValue]) : Option[Map[String, JsValue]]

    def deleteObject(obj : DBObject, db_name : String, primary_key : String) : Unit
    def deleteMultiObject(obj : DBObject, db_name : String) : Unit
    def getOneDBAllCollectionNames : scala.collection.mutable.Set[String]

    def restoreDatabase() = ???
    def dumpDatabase() = ???

    def mapReduceJob(c : String, m : String, r : String, q : Option[DBObject], out : String) : Boolean = false
}
