package com.pharbers.macros.convert.mongodb

import com.mongodb.casbah.Imports.DBObject

trait TraitRequest {
    var res: String

    def cond2QueryObj(): DBObject

    def cond2UpdateObj(): DBObject

    def cond2fmQueryObj(): (Int, Int)
}
