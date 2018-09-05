package com.pharbers.macros.convert.mongodb

import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.mongodb.dbconnect.ConnectionInstance
import com.pharbers.mongodb.dbinstance.dbInstanceManager

trait MongoMacro[R <: TraitRequest] extends dbInstanceManager[R] {
    override def instance(ci: ConnectionInstance): DBTrait[R] = new mongoDBImpl[R](ci)
}