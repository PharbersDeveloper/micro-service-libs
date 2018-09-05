package com.pharbers.pattern.request

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._
import com.pharbers.macros.convert.mongodb.TraitRequest

@One2ManyConn[eqcond]("eqcond")
@One2ManyConn[upcond]("upcond")
@One2OneConn[fmcond]("fmcond")
@ToStringMacro
class request extends commonEntity with TraitRequest {
    override var res: String = ""

    override def cond2QueryObj(): DBObject = {
        var o: DBObject = DBObject()
        eqcond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        o
    }

    override def cond2UpdateObj(): DBObject = {
        var o: DBObject = DBObject()
        upcond.getOrElse(Nil).foreach { x =>
            if (x.isUpdateCond) {
                o ++= x.cond2UpdateDBObectj()
            }
        }
        o
    }

    override def cond2fmQueryObj(): (Int, Int) = (fmcond.get.skip, fmcond.get.take)

}
