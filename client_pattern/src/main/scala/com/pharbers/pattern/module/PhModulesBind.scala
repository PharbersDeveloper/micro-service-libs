package com.pharbers.pattern.module

import play.api.inject.{Binding, Module}
import com.pharbers.models.request.request
import com.pharbers.driver.PhRedisDriverImpl
import play.api.{Configuration, Environment}
import com.pharbers.mongodb.dbinstance.dbInstanceManager

class PhModulesBind extends Module{
	override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
		Seq(
			bind[dbInstanceManager[request]].to[DBManagerModule]
			,bind[PhRedisDriverImpl].to[RedisManagerModule]
		)
	}
}
