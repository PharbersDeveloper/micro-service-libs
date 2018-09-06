package com.pharbers.driver.redis.hash

import com.redis.RedisClient

/**
  * Created by jeorch on 17-11-21.
  */
trait hashDriverTrait {
    protected val conn : RedisClient

    def hget(key : Any, field : Any) : String = {
        conn.hget(key, field).getOrElse(throw new Exception("Redis hget Error"))
    }

    def hgetall(key : Any) : Map[String, String] = {
        val h = conn.hgetall1(key)
        h.getOrElse(throw new Exception("Redis hgetall Error"))
    }

}
