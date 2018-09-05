package com.pharbers.pattern.mongo

import com.pharbers.macros.convert.mongodb.{MongoMacro, TraitRequest}
import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.pattern.request.request

class mongoImpl() extends MongoMacro[request]

object test_db_inst {
    implicit def db_inst: DBTrait[TraitRequest] = new mongoImpl().queryDBInstance("test").get.asInstanceOf[DBTrait[TraitRequest]]
}

object client_db_inst {
    implicit def db_inst: DBTrait[TraitRequest] = new mongoImpl().queryDBInstance("client").get.asInstanceOf[DBTrait[TraitRequest]]
}

object tm_report_db_inst {
    implicit def db_inst: DBTrait[TraitRequest] = new mongoImpl().queryDBInstance("tm_report").get.asInstanceOf[DBTrait[TraitRequest]]
}
