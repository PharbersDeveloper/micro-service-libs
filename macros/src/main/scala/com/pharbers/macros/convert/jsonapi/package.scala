package com.pharbers.macros.convert

import com.pharbers.jsonapi.model.Included
import com.pharbers.jsonapi.model.RootObject.ResourceObject

package object jsonapi {

    def fromResourceObject[T: ResourceObjectReader](resource: ResourceObject, included: Option[Included]): T =
        implicitly[ResourceObjectReader[T]].fromResourceObject(resource, included)
    def toResourceObject[T: ResourceObjectReader](entity: T): (ResourceObject, Included) =
        implicitly[ResourceObjectReader[T]].toResourceObject(entity)
}
