package com.pharbers.models.request

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro
import com.pharbers.macros.convert.mongodb.TraitConditions
import org.bson.types.ObjectId

@ToStringMacro
// 大于条件
case class gtcond() extends commonEntity with TraitConditions {
	var key: String = ""
	var `val`: Any = null
	var category: Any = null

	override def cond2QueryDBObject(): DBObject = DBObject(key -> DBObject("$gt" -> `val`))

	override def cond2UpdateDBObectj(): DBObject = DBObject()

	override def isQueryCond: Boolean = true

	override def isUpdateCond: Boolean = false
}
