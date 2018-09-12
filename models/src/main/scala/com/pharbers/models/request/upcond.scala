package com.pharbers.models.request

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro
import com.pharbers.macros.convert.mongodb.TraitConditions

@ToStringMacro
case class upcond() extends commonEntity with TraitConditions {
	var key: String = ""
	var `val`: Any = null

	override def cond2QueryDBObject(): DBObject = DBObject()

	override def cond2UpdateDBObectj(): DBObject = DBObject(key -> `val`)

	override def isQueryCond: Boolean = false

	override def isUpdateCond: Boolean = true
}
