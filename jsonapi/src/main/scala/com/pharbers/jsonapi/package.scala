package com.pharbers

import com.pharbers.jsonapi.model._
import com.pharbers.jsonapi.model.RootObject.ResourceObject

package object jsonapi {

    trait JsonapiRelationshipObjectWriter[A] {
        def toJsonapi(a : A) : Relationship
    }

    trait JsonapiRelationshipObjectReader[A] {
        def fromJsonapi(relationshipObject : Relationships, included : Included) : A
    }

    trait JsonapiReourceObjectWriter[A] {
        def toJsonapi(a : A) : Attributes
    }

    trait JsonapiResourceObjectReader[A] {
        def fromJsonapi(rootObject : ResourceObject) : A
    }

    trait JsonapiRootObjectWriter[A] {
        def toJsonapi(a: A): RootObject
    }

    trait JsonapiRootObjectReader[A] {
        def fromJsonapi(rootObject: RootObject) : A
    }

    trait JsonapiRootObjectFormat[A] extends JsonapiRootObjectWriter[A] with JsonapiRootObjectReader[A]
    trait JsonapiResourceObjectFormat[A] extends JsonapiReourceObjectWriter[A] with JsonapiResourceObjectReader[A]
    trait JsonapiRelationshipObjectFormat[A] extends JsonapiRelationshipObjectWriter[A] with JsonapiRelationshipObjectReader[A]

    implicit class ToJsonapiRootObjectWriterOps[A](a: A) {
        def rootObject(implicit writer: JsonapiRootObjectWriter[A]): RootObject = {
            Jsonapi.asRootObject(a)
        }
    }

    implicit class FromJsonapiRootObjectReaderOps(rootObject: RootObject) {
        def jsonapi[A](implicit reader: JsonapiRootObjectReader[A]): A = {
            Jsonapi.fromRootObject(rootObject)
        }
    }

    object Jsonapi {
        def asRootObject[A](a: A)(implicit writer: JsonapiRootObjectWriter[A]): RootObject = writer toJsonapi a
        def fromRootObject[A](rootObject: RootObject)(implicit reader: JsonapiRootObjectReader[A]): A = reader fromJsonapi rootObject
    }
}
