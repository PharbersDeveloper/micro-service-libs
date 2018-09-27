package com.pharbers.models.request

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._
import com.pharbers.macros.convert.mongodb.TraitRequest

@One2ManyConn[eqcond]("eqcond")
@One2ManyConn[necond]("necond")
@One2ManyConn[gtcond]("gtcond")
@One2ManyConn[gtecond]("gtecond")
@One2ManyConn[ltcond]("ltcond")
@One2ManyConn[ltecond]("ltecond")
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
        necond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        gtcond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        gtecond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        ltcond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        ltecond.getOrElse(Nil).foreach { x =>
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

    override def cond2fmQueryObj(): (Int, Int) = {
        val tmp = fmcond.getOrElse {
            val tmp = new fmcond
            tmp.skip = 0
            tmp.take = 20
            tmp
        }
        (tmp.skip, tmp.take)
    }
}
