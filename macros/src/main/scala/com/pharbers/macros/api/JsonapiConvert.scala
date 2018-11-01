package com.pharbers.macros.api

import com.pharbers.jsonapi.model.RootObject

trait JsonapiConvert[T] {
    def fromJsonapi(jsonapi: RootObject): T
    def fromJsonapiLst(jsonapi: RootObject): List[T]
    def toJsonapi(obj: T): RootObject
    def toJsonapi(objs: List[T]): RootObject
}