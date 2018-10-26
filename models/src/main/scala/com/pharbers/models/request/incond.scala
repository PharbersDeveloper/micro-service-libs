package com.pharbers.models.request

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro
import com.pharbers.macros.convert.mongodb.TraitConditions
import org.bson.types.ObjectId

@ToStringMacro
// in
case class incond() extends commonEntity with TraitConditions {
    var key: String = ""
    var `val`: Iterable[Any] = null
    var category: Any = null

    //old

//    override def cond2QueryDBObject(): DBObject = key match {
//        case "id" => DBObject("_id" -> DBObject("$in" -> `val`))
//        case "_id" => DBObject("_id" -> DBObject("$in" -> `val`))
//        case _ => DBObject(key ->  DBObject("$in" -> `val`))
//    }

    override def cond2QueryDBObject(): DBObject = key match {
        case "id" =>
            DBObject("_id" -> DBObject("$in" -> `val`.map(x => new ObjectId(x.toString)).toSet))
        case "_id" =>
            DBObject("_id" -> DBObject("$in" -> `val`.map(x => new ObjectId(x.toString)).toSet))
        case _ => DBObject(key ->  DBObject("$in" -> `val`))
    }


    override def cond2UpdateDBObectj(): DBObject = DBObject()

    override def isQueryCond: Boolean = true

    override def isUpdateCond: Boolean = false
}
