package com.pharbers.dbManagerTrait

import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.cliTraits.DBTrait
import com.pharbers.moduleConfig.{ConfigDefines, ConfigImpl}
import com.pharbers.mongodbConnect.connection_instance
import com.pharbers.mongodbDriver.MongoDB.{MongoDBImpl, _data_connection}

import scala.xml.Node

trait dbInstanceManager extends PharbersInjectModule {

    class MongoDBInstance(tmp : connection_instance) extends MongoDBImpl {
        override implicit val dc: connection_instance = tmp
    }

    override val id: String = "mongodb-connect-nodes"
    override val configPath: String = "pharbers_config/db_manager.xml"
    override val md = "connect-config-path" :: Nil //"server_host" :: "server_port" :: "connect_name" :: "connect_pwd" :: "conn_name" :: Nil

    import com.pharbers.moduleConfig.ModuleConfig.fr
    implicit val f : (ConfigDefines, Node) => ConfigImpl = { (c, n) =>
        ConfigImpl(
            c.md map { x => x -> ((n \ x).toList map { iter =>
                (iter \\ "@name").toString -> new dbInstance((iter \\ "@value").toString)
            })}
        )}
    override lazy val config: ConfigImpl = loadConfig(configDir + "/" + configPath)

    lazy val connections : List[(String, DBTrait)] =
        config.mc.find(p => p._1 == md.head).get._2.
            asInstanceOf[List[(String, connection_instance)]].
                map (iter => iter._1 -> new MongoDBInstance(iter._2))

    def queryDBInstance(name : String) : Option[DBTrait] =
        connections.find(p => p._1 == name).map (x => Some(x._2)).getOrElse(None)

    def queryDBConnection(name : String) : Option[connection_instance] =
        connections.find(p => p._1 == name).
            map (x => Some(x._2.asInstanceOf[MongoDBInstance].dc)).getOrElse(None)
}
