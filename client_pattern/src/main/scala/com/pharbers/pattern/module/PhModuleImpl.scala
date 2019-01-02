package com.pharbers.pattern.module

import javax.inject.Singleton
import com.pharbers.models.request.request
import com.pharbers.driver.PhRedisDriverImpl
import com.pharbers.driver.util.redis_conn_cache
import com.pharbers.macros.convert.mongodb.MongoMacro

@Singleton
class DBManagerModule extends MongoMacro[request]

@Singleton
class RedisManagerModule extends redis_conn_cache with PhRedisDriverImpl


