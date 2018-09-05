package com.pharbers.macros.api

import com.mongodb.DBObject

import scala.reflect.ClassTag

trait MongoDBConvert[T] {

//    def queryObject(res: request)(implicit dbTrait: DBTrait): Option[T]
//    def queryMultipleObject[T: ClassTag](res: request, sort : String = "date"): List[T]
//    def insertObject[T: ClassTag](model: T): DBObject
//    def updateObject[T: ClassTag](res: request): Int
//    def deleteObject(res: request): Int
//    def queryCount: Long = ???
//
//    def insert()
//    def fromMongo(mongo: DBObject): T
//    def toMongo(obj: T): DBObject
}