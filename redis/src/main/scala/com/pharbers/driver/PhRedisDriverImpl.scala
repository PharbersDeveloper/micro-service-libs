package com.pharbers.driver

import com.pharbers.driver.util.{redis_conn_instance, PhRedisTrait}

/**
  * Created by jeorch on 17-12-13.
  */
trait PhRedisDriverImpl extends PhRedisTrait {
    implicit val conn_instance : redis_conn_instance

    override def addString(key: Any, value: String): Unit =
        conn_instance.getConnection(c => c.set(key, value))

    override def addListLeft(key: Any, values: Any*): Unit =
        conn_instance.getConnection(c => values.foreach(c.lpush(key, _)))

    override def addListRight(key: Any, values: Any*): Unit =
        conn_instance.getConnection(c => values.foreach(c.rpush(key, _)))

    override def addMap(key: Any, values: Map[Any, Any]): Unit =
        conn_instance.getConnection(c => c.hmset(key, values))

    override def addMap(key: Any, mapK: Any, mapV: Any): Unit =
        conn_instance.getConnection(c => c.hset(key, mapK, mapV))

    override def addSet(key: Any, values: Set[Any]): Unit =
        conn_instance.getConnection(c => values.foreach(c.sadd(key, _)))

    override def addSet(key: Any, values: Any*): Unit =
        conn_instance.getConnection(c => values.foreach(c.sadd(key, _)))

    override def delete(key: Any, keys: Any*) : Long = {
        conn_instance.getConnection(c => c.del(key, keys:_*) match {
            case None => throw new Exception("cannot get this value from redis")
            case value: Option[Long] => value.get
        })
    }

    override def getString(key: Any) : String = {
        conn_instance.getConnection(c => c.get(key) match {
            case None => null
            case value: Option[String] => value.get
        })
    }

    override def getListAllValue(key: Any): List[String] = {
        conn_instance.getConnection(c => c.lrange(key, 0, -1) match {
            case None => List.empty
            case value: Option[List[Option[String]]] => value.get.map(x => x.get)
        })
    }

    override def getListLeftValue(key: Any): String = {
        conn_instance.getConnection(c => c.lpop(key) match {
            case None => null
            case value: Option[String] => value.get
        })
    }

    override def getListRightValue(key: Any): String = {
        conn_instance.getConnection(c => c.rpop(key) match {
            case None => null
            case value: Option[String] => value.get
        })
    }

    override def getListSize(key: Any): Long = {
        conn_instance.getConnection(c => c.llen(key) match {
            case None => 0L
            case value: Option[Long] => value.get
        })
    }

    override def getMapAllValue(key: Any): Map[String, String] = {
        conn_instance.getConnection(c => c.hgetall1(key) match {
            case None => Map.empty
            case value: Option[Map[String, String]] => value.get
        })
    }

    override def getMapSize(key: Any): Long = {
        conn_instance.getConnection(c => c.hlen(key) match {
            case None => 0L
            case value: Option[Long] => value.get
        })
    }

    override def getMapValue(key: Any, mapK: Any): String = {
        conn_instance.getConnection(c => c.hget(key, mapK) match {
            case None => null
            case value: Option[String] => value.get
        })
    }

    override def getSetAllValue(key: Any): Set[String] = {
        conn_instance.getConnection(c => c.smembers(key) match {
            case None => Set.empty
            case value: Option[Set[Option[String]]] => value.get.map(x => x.get)
        })
    }

    override def getSetLastValue(key: Any): String = {
        conn_instance.getConnection(c => c.spop(key) match {
            case None => null
            case value: Option[String] => value.get
        })
    }

    override def getSetSize(key: Any): Long = {
        conn_instance.getConnection(c => c.scard(key) match {
            case None => 0L
            case value: Option[Long] => value.get
        })
    }

    override def exsits(key: Any): Boolean = {
        conn_instance.getConnection(c => c.exists(key))
    }

    override def expire(key: Any, ttl: Int): Unit = {
        conn_instance.getConnection(c => c.expire(key, ttl))
    }

    override def flush: Unit = {
        conn_instance.getConnection(c => c.flushall)
    }
}