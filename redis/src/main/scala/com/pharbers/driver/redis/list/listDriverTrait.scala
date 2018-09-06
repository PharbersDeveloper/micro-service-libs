package com.pharbers.driver.redis.list

import com.redis.RedisClient

/**
  * Created by jeorch on 17-11-20.
  */
trait listDriverTrait {
    protected val conn : RedisClient

    def lpush(key : Any, value : Any, values : Any*) : Unit = {
        conn.lpush(key, value, values)
    }
    def rpush(key : Any, value : Any, values : Any*) : Unit = {
        conn.rpush(key, value, values)
    }
    def lpop(key : Any) : String = {
        val l = conn.lpop(key)
        l.getOrElse(throw new Exception("Redis lpop Error"))
    }
    def rpop(key : Any) : String = {
        val r = conn.rpop(key)
        r.getOrElse(throw new Exception("Redis rpop Error"))
    }
    def lindex(key : Any, index : Int) : String = {
        val l = conn.lindex(key, index)
        l.getOrElse(throw new Exception("Redis lindex Error"))
    }
    def ltrim(key : Any, start : Int, end : Int) : Boolean = {
        conn.ltrim(key, start, end)
    }
    def llen(key : Any) : Long = {
        val l = conn.llen(key)
        l.getOrElse(throw new Exception("Redis llen Error"))
    }
    def lrange(key : Any, start : Int, end : Int) : List[String] = {
        val l = conn.lrange(key, start, end)
        val lst = l.getOrElse(throw new Exception("Redis lrange-listOpt Error"))
        lst.map(x => x.getOrElse(throw new Exception("Redis llen-listStr Error")))
    }

}
