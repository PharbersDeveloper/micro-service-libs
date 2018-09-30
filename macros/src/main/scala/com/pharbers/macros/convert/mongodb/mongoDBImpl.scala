package com.pharbers.macros.convert.mongodb

import com.mongodb.casbah.Imports._
import scala.reflect.{ClassTag, classTag}
import com.mongodb.casbah.Imports.DBObject
import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.mongodb.dbconnect.ConnectionInstance

class mongoDBImpl[R <: TraitRequest](override val di: ConnectionInstance) extends DBTrait[R] with dbutil {
    def queryObject[T: ClassTag](res: R): Option[T] = {
        val coll = di.getCollection(res.res)
        val conditions = res.cond2QueryObj()
        val className = classTag[T].toString()
        val reVal = coll.findOne(conditions)
        if (reVal.isEmpty) None else {
            Some(DBObjectBindObject(coll.findOne(conditions), className).asInstanceOf[T])
        }
    }

    def queryMultipleObject[T: ClassTag](res: R, sort: String = "date"): List[T] = {
        val coll = di.getCollection(res.res)
        val conditions = res.cond2QueryObj()
        val className = classTag[T].toString()
        val t = coll.find(conditions).sort(DBObject(sort -> -1)).
                skip(res.cond2fmQueryObj()._1).
                take(res.cond2fmQueryObj()._2).toList
        val result = t.map(x => DBObjectBindObject(Some(x), className).asInstanceOf[T])
        result
    }

    def insertObject[T: ClassTag](model: T): DBObject = {
        val coll = di.getCollection(loadJsonApiType(model))
        val dbo = Struct2DBObject(model)
        coll.insert(dbo)
        dbo
    }

    def updateObject[T: ClassTag](res: R): Int = {
        val coll = di.getCollection(res.res)
        val conditions = res.cond2QueryObj()
        val updateData = res.cond2UpdateObj()

        val className = classTag[T].toString()
        val reVal = coll.findOne(conditions)
        if (reVal.isDefined) {
            val find = DBObjectBindObject(reVal, className).asInstanceOf[T]
            val dbo = Struct2DBObject(find) ++ updateData
            val result = coll.update(conditions, dbo)
            result.getN
        } else {
            0
        }
    }

    def deleteObject(res: R): Int = {
        val coll = di.getCollection(res.res)
        val conditions = res.cond2QueryObj()
        val result = coll.remove(conditions)
        result.getN
    }

    def queryCount: Long = ???

}
