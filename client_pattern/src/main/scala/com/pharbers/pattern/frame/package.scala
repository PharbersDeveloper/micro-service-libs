package com.pharbers.pattern

import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model

package object frame {
    implicit class checkResult(str: String) extends CirceJsonapiSupport {
        def check(): String = {
            val jsonapi = decodeJson[model.RootObject](parseJson(str))
            jsonapi.errors match {
                case Some(error) => throw new Exception(error.head.title.getOrElse("unknown error"))
                case None => str
            }
        }
    }
}
