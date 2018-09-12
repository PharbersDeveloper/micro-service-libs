package com.pharbers.models.request

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro
import com.pharbers.macros.convert.mongodb.TraitConditions

@ToStringMacro
case class fmcond() extends commonEntity with TraitConditions {
	var skip: Int = 0
	var take: Int = 20

	override def cond2QueryDBObject(): DBObject = DBObject()

	override def cond2UpdateDBObectj(): DBObject = DBObject()

	override def isQueryCond: Boolean = true

	override def isUpdateCond: Boolean = false

	def queryConnect(): DBObject = DBObject()
}
