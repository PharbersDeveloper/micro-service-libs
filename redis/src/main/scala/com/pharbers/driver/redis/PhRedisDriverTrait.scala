package com.pharbers.driver.redis

import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.driver.redis.hash.hashDriverTrait
import com.pharbers.driver.redis.list.listDriverTrait
import com.pharbers.driver.redis.set.setDriverTrait
import com.redis.RedisClient

/**
  * Created by jeorch on 17-11-17.
  */
trait PhRedisDriverTrait extends PharbersInjectModule {
    override val id: String = "redis-config"
    override val configPath: String = "pharbers_config/redis_config.xml"
    override val md = "host":: "port" :: Nil

    val host = config.mc.find(p => p._1 == "host").get._2.toString
    val port = config.mc.find(p => p._1 == "port").get._2.toString.toInt

    case class listDriverImpl(override val conn : RedisClient) extends listDriverTrait
    case class setDriverImpl(override val conn : RedisClient) extends setDriverTrait
    case class hashDriverImpl(override val conn : RedisClient) extends hashDriverTrait

    lazy val commonDriver = new RedisClient(host, port)
    lazy val phListDriver = listDriverImpl(commonDriver)
    lazy val phSetDriver = setDriverImpl(commonDriver)
    lazy val phHashDriver = hashDriverImpl(commonDriver)

    def del(key : Any, keys : Any*) : Unit = {
        commonDriver.del(key, keys)
    }

    def flushall = commonDriver.flushall

}

case class phRedisDriver() extends PhRedisDriverTrait


