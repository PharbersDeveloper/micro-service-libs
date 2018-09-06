package com.pharbers.driver.util

import com.pharbers.baseModules.PharbersInjectModule
import com.redis.{RedisClient, RedisClientPool}

/**
  * Created by jeorch on 17-12-13.
  */
trait redis_conn_instance extends PharbersInjectModule {
    override val id: String = "redis-config"
    override val configPath: String = "pharbers_config/redis_config.xml"
    override val md = "host":: "port" :: Nil

    def host = config.mc.find(p => p._1 == "host").get._2.toString
    def port = config.mc.find(p => p._1 == "port").get._2.toString.toInt

    lazy val _pool = new RedisClientPool(host, port)
    def getConnection[A](func : RedisClient => A) = _pool.withClient(client => func(client))
}
