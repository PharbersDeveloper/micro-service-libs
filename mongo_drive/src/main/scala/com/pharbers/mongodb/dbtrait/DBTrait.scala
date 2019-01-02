package com.pharbers.mongodb.dbtrait

import scala.reflect.ClassTag
import com.mongodb.casbah.Imports._
import com.pharbers.mongodb.dbconnect.ConnectionInstance

trait DBTrait[R] {
    implicit val di: ConnectionInstance

    def queryObject[T: ClassTag](res: R): Option[T]

    def queryMultipleObject[T: ClassTag](res: R, sort: String = "date"): List[T]

    def insertObject[T: ClassTag](model: T): DBObject

    def updateObject[T: ClassTag](res: R): Int

    def deleteObject(res: R): Int

    def queryCount(res: R): Long
}
