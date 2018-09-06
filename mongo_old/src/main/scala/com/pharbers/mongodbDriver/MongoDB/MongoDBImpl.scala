package com.pharbers.mongodbDriver.MongoDB

import com.pharbers.cliTraits.DBTrait
import com.pharbers.mongodbConnect.{connection_instance, from}
import play.api.libs.json.JsValue
import com.mongodb.casbah.Imports._

trait MongoDBImpl extends DBTrait {
    implicit val dc : connection_instance
    // = _data_connection

    override def insertObject(obj : DBObject, db_name : String, primary_key : String) : Unit = {
        val primary = obj.get(primary_key) //.map (x => x).getOrElse(throw new Exception("get primary key error"))
        (from db() in db_name where (primary_key -> primary) select(x => x)).toList match {
            case Nil => dc.getCollection(db_name) += obj
            case _ => throw new Exception("primary key error")
        }
    }

    override def updateObject(obj : DBObject, db_name : String, primary_key : String) : Unit = {
        val primary = obj.get(primary_key) //.map (x => x).getOrElse(throw new Exception("get primary key error"))
        (from db() in db_name where (primary_key -> primary) select(x =>x)).toList match {
            case head :: Nil => dc.getCollection(db_name).update(head, obj)
            case _ => throw new Exception("primary key error")
        }
    }

    override def queryObject(condition : DBObject, db_name : String)
                   (implicit t : DBObject => Map[String, JsValue]) : Option[Map[String, JsValue]] = {

        (from db() in db_name where condition).selectTop(1)("date")(x => t(x)).toList match {
            case Nil => None
            case head :: Nil => Some(head)
            case _ => throw new Exception("data duplicate")
        }
    }

    override def queryMultipleObject(condition : DBObject, db_name : String, sort : String = "date", skip : Int = 0, take : Int = 20)
                           (implicit t : DBObject => Map[String, JsValue]) : List[Map[String, JsValue]] = {
        (from db() in db_name where condition).selectSkipTop(skip)(take)(sort)(x => t(x)).toList
    }

    override def queryMultipleWithOutSort(condition : DBObject, db_name : String, skip : Int = 0, take : Int = 20)
                                (implicit t : DBObject => Map[String, JsValue]) : List[Map[String, JsValue]] = {

        (from db() in db_name where condition).selectSkipTopWithoutSort(skip)(take)(x => t(x)).toList
    }

    override def queryCount(condition : DBObject, db_name : String)
                           (implicit t : DBObject => Map[String, JsValue]) : Option[Int] = {
        Some((from db() in db_name where condition).count)
    }

    override def deleteObject(obj: DBObject, db_name: String, primary_key: String): Unit = {
        val primary = obj.get(primary_key) //.map (x => x).getOrElse(throw new Exception("get primary key error"))
        (from db() in db_name where (primary_key -> primary) select(x =>x)).toList match {
            case head :: Nil => dc.getCollection(db_name) -= head
            case _ => throw new Exception("primary key error")
        }
    }

    override def deleteMultiObject(obj: DBObject, db_name: String): Unit = {
        (from db() in db_name where obj select(x =>x)).toList map { iter =>
            dc.getCollection(db_name) -= iter
        }
    }

    override def getOneDBAllCollectionNames: scala.collection.mutable.Set[String] = dc.getAllCollectionNames

    override def querySum(condition : DBObject, db_name : String)
                         (sum : (Map[String, JsValue], Map[String, JsValue]) => Map[String, JsValue])
                         (acc: (DBObject) => Map[String, JsValue]) : Option[Map[String, JsValue]] = {

        val c = from db() in db_name where condition selectCursor

        var result : Map[String, JsValue] = Map.empty
        while (c.hasNext) {
            result = sum(result, acc(c.next()))
        }

        if (result.isEmpty) None
        else Some(result)
    }

    override def aggregate(condition : DBObject, db_name : String, group : DBObject)
                 (implicit t : DBObject => Map[String, JsValue]) : Option[Map[String, JsValue]] =
        Some(t((from db() in db_name where condition).aggregate(group)))

    override def mapReduceJob(c : String, m : String, r : String, q : Option[DBObject], out : String) : Boolean =
        from.mapReduce(c, m, r, q, out)

}