package com.pharbers.pattern.common

import com.pharbers.driver.PhRedisDriver
import com.pharbers.jsonapi.model
import com.pharbers.models.entity.user
import com.pharbers.models.service.auth
import com.pharbers.pattern.module.RedisManagerModule
import play.api.mvc.Request

trait parseToken {
    def parseToken(request: Request[model.RootObject])(implicit rd: RedisManagerModule): auth = {
        val token = request.headers.get("Authorization")
                .getOrElse(throw new Exception("token parse error"))
                .split(" ").last

        val rd = new PhRedisDriver()

        if(!rd.exsits(token)) throw new Exception("token expired")

        val u = new user()
        u.id = rd.getMapValue(token, "user_id")
        u.email = rd.getMapValue(token, "email")
        u.user_name = rd.getMapValue(token, "user_name")
        val a = new auth()
        a.token = token
        a.user = Some(u)
        a
    }
}