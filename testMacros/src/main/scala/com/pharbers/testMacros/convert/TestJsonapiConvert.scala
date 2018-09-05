package com.pharbers.testMacros.convert

import com.pharbers.macros.JsonapiConvert
import com.pharbers.testMacros.model.user
import com.pharbers.util.log.phLogTrait

class TestJsonapiConvert() extends JsonapiConvert[user] with phLogTrait {

    import com.pharbers.jsonapi.model.RootObject._
    import com.pharbers.jsonapi.model._
    import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._
    import com.pharbers.macros.convert.jsonapi._

    override def fromJsonapi(jsonapi: RootObject): user = {

        val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
        val included = jsonapi.included
        val entity = fromResourceObject[user](jsonapi_data, included)(ResourceReaderMaterialize)
//        phLog("entity in fromJsonapi is ===> " + entity)

        entity
    }

    override def toJsonapi(obj: user): RootObject = {
        val reo_includeds = toResourceObject(obj)

        RootObject(
            data = Some(reo_includeds._1),
            included = if(reo_includeds._2.resourceObjects.array.isEmpty) None else Some(reo_includeds._2)
        )
    }

    override def toJsonapi(objs: List[user]): RootObject = {
        val data_included_lst = objs.map(toJsonapi)
        val dataLst = data_included_lst.map(_.data).filter(_.isDefined).map(_.get.asInstanceOf[ResourceObject])
        val includedLst = data_included_lst.map(_.included).filter(_.isDefined).flatMap(x => x.get.resourceObjects.array).distinct
        RootObject(
            data = if(dataLst.isEmpty) None else Some(ResourceObjects(dataLst)),
            included = if(includedLst.isEmpty) None else Some(Included(ResourceObjects(includedLst)))
        )
    }
}
