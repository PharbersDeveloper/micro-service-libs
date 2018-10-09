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
        this.eqcond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        this.necond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        this.gtcond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        this.gtecond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        this.ltcond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        this.ltecond.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        o
    }

    override def cond2UpdateObj(): DBObject = {
        var o: DBObject = DBObject()
        this.upcond.getOrElse(Nil).foreach { x =>
            if (x.isUpdateCond) {
                o ++= x.cond2UpdateDBObectj()
            }
        }
        o
    }

    override def cond2fmQueryObj(): (Int, Int) = {
        val tmp = this.fmcond.getOrElse(new fmcond)
        (tmp.skip, tmp.take)
    }
}
