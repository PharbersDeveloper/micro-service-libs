package com.pharbers.driver.redis.set

import com.redis.RedisClient

/**
  * Created by jeorch on 17-11-21.
  */
trait setDriverTrait {
    protected val conn : RedisClient

    def sadd(key : Any, value : String) : Unit = {
        conn.sadd(key, value)
    }

    def sadd(key : Any, value : Map[String, Any], func : (Map[String, Any], Map[String, Any]) => Map[String, Any]) : Unit = {
        val hash_key = value.getOrElse(key.toString, throw new Exception("Redis sadd hash_key not defined Error"))
        var map = value
        if(conn.sismember(key, hash_key)){
            val m = conn.hgetall1(hash_key).getOrElse(throw new Exception("Redis sadd hgetall1 Error"))
            map = func(m, value)
        }
        conn.sadd(key, hash_key)
        map.filterNot(x => x._1==key).foreach(x => conn.hset(hash_key, x._1, x._2))
    }

    def smembers(key : Any) : Set[String] = {
        val s = conn.smembers(key)
        val slst = s.getOrElse(throw new Exception("Redis smembers_setOpt Error"))
        slst.map(x => x.getOrElse(throw new Exception("Redis smembers_setStr Error")))
    }

}
