package com.pharbers.mongodb.dbinstance

import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.moduleConfig.{ConfigDefines, ConfigImpl}
import com.pharbers.mongodb.dbconnect.{ConnectionInstance, dbInstance}

import scala.xml.Node

trait dbInstanceManager[R] extends PharbersInjectModule {
    def instance(ci: ConnectionInstance): DBTrait[R]

    override val id: String = "mongodb-connect-nodes"
    override val configPath: String = "pharbers_config/db_manager.xml"
    override val md: List[String] = "connect-config-path" :: Nil

    import com.pharbers.moduleConfig.ModuleConfig.fr

    implicit val f: (ConfigDefines, Node) => ConfigImpl = { (c, n) =>
        ConfigImpl(
            c.md map { x =>
                x -> ((n \ x).toList map { iter =>
                    (iter \\ "@name").toString -> new dbInstance((iter \\ "@value").toString)
                })
            }
        )
    }
    override lazy val config: ConfigImpl = loadConfig(configDir + "/" + configPath)

    lazy val connections: List[(String, DBTrait[R])] =
        config.mc.find(p => p._1 == md.head).get._2
                .asInstanceOf[List[(String, ConnectionInstance)]]
                .map(iter => iter._1 -> instance(iter._2))

    def queryDBInstance(name: String): Option[DBTrait[R]] =
        connections.find(p => p._1 == name).flatMap(x => Some(x._2))

    def queryDBConnection(name: String): Option[ConnectionInstance] =
        connections.find(p => p._1 == name).flatMap(x => Some(x._2.asInstanceOf[DBTrait[R]].di))
}
