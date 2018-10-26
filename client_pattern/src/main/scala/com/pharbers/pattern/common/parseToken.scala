package com.pharbers.pattern.common

import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model
import com.pharbers.jsonapi.model.RootObject
import com.pharbers.models.service.auth
import com.pharbers.pattern.module.RedisManagerModule
import play.api.mvc.Request

trait parseToken { this: CirceJsonapiSupport =>
    def parseToken(request: Request[model.RootObject])(implicit rd: RedisManagerModule): auth = {

        import com.pharbers.macros._
        import com.pharbers.macros.convert.jsonapi.JsonapiMacro._

        val token = request.headers.get("Authorization")
                .getOrElse(throw new Exception("token parse error"))
                .split(" ").last

        if(!rd.exsits(token))
            throw new Exception("token expired")

        formJsonapi[auth](decodeJson[RootObject](parseJson(rd.getString(token))))
    }
}