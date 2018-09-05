package com.pharbers.macros.convert.mongodb

import com.mongodb.casbah.Imports.DBObject

trait TraitConditions {
	def cond2QueryDBObject(): DBObject = DBObject()
	def cond2UpdateDBObectj(): DBObject = DBObject()
	def isQueryCond: Boolean = false
	def isUpdateCond: Boolean = false
}
