package com.pharbers.models.request

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._
import com.pharbers.macros.convert.mongodb.{TraitConditions, TraitRequest}

@One2ManyConn[eqcond]("eqcond")
@One2ManyConn[necond]("necond")
@One2ManyConn[gtcond]("gtcond")
@One2ManyConn[gtecond]("gtecond")
@One2ManyConn[ltcond]("ltcond")
@One2ManyConn[ltecond]("ltecond")
@One2ManyConn[upcond]("upcond")
@One2ManyConn[incond]("incond")
@One2OneConn[fmcond]("fmcond")
@ToStringMacro
class request extends commonEntity with TraitRequest {
    override var res: String = ""

    override def cond2QueryObj(): DBObject = {
        implicit val cond2dbo: Option[List[TraitConditions]] => DBObject = { conds =>
            var obj: DBObject = DBObject()
            conds.getOrElse(Nil).foreach{ cond =>
                obj ++= cond.cond2QueryDBObject
            }
            obj
        }

        def mergeCond(obj: DBObject)(dbo: DBObject): DBObject = {
            obj.foreach{ cur =>
                if (dbo.contains(cur._1)){
                    val tmp = dbo.remove(cur._1).get
                    dbo ++= $and(DBObject(cur._1 -> tmp), DBObject(cur))
                }
                else
                    dbo ++= DBObject(cur)
            }
            dbo
        }


        var obj: DBObject = DBObject()
        obj = mergeCond(this.eqcond)(obj)
        obj = mergeCond(this.necond)(obj)
        obj = mergeCond(this.gtcond)(obj)
        obj = mergeCond(this.gtecond)(obj)
        obj = mergeCond(this.ltcond)(obj)
        obj = mergeCond(this.ltecond)(obj)
        obj = mergeCond(this.incond)(obj)
        obj
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
