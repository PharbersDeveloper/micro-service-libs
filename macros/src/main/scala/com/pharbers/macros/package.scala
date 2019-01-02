package com.pharbers

import com.mongodb.DBObject
import scala.reflect.ClassTag
import com.pharbers.macros.api.errorEntity
import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.jsonapi.model.{Errors, RootObject}
import com.pharbers.macros.convert.mongodb.TraitRequest
import com.pharbers.jsonapi.model.{Error, Errors, RootObject}

package object macros {
    type JsonapiConvert[T] = com.pharbers.macros.api.JsonapiConvert[T]

    def formJsonapi[T: JsonapiConvert](jsonapi: RootObject): T =
        implicitly[JsonapiConvert[T]].fromJsonapi(jsonapi)

    def formJsonapiLst[T: JsonapiConvert](jsonapi: RootObject): List[T] =
        implicitly[JsonapiConvert[T]].fromJsonapiLst(jsonapi)

    def toJsonapi[T: JsonapiConvert](obj: T): RootObject =
        implicitly[JsonapiConvert[T]].toJsonapi(obj)

    def toJsonapi[T: JsonapiConvert](objLst: List[T]): RootObject =
        implicitly[JsonapiConvert[T]].toJsonapi(objLst)

    def toErrorsJsonapi(error: errorEntity): RootObject =
        RootObject(errors = Some((error.toError :: Nil).asInstanceOf[Errors]))

    def toErrorsJsonapi(errors: List[errorEntity]): RootObject =
        RootObject(errors = Some(errors.map(_.toError).asInstanceOf[Errors]))

    def queryObject[T: ClassTag](res: TraitRequest)(implicit dbt: DBTrait[TraitRequest]): Option[T] = dbt.queryObject[T](res)

    def queryMultipleObject[T: ClassTag](res: TraitRequest, sort: String = "date")(implicit dbt: DBTrait[TraitRequest]): List[T] = dbt.queryMultipleObject(res, sort)

    def insertObject[T: ClassTag](model: T)(implicit dbt: DBTrait[TraitRequest]): DBObject = dbt.insertObject(model)

    def updateObject[T: ClassTag](res: TraitRequest)(implicit dbt: DBTrait[TraitRequest]): Int = dbt.updateObject(res)

    def deleteObject(res: TraitRequest)(implicit dbt: DBTrait[TraitRequest]): Int = dbt.deleteObject(res)

    def queryCount(res: TraitRequest)(implicit dbt: DBTrait[TraitRequest]): Long = dbt.queryCount(res)

}
