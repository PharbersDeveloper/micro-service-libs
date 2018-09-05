package com.pharbers.mongodb.dbconnect

import com.mongodb.casbah.Imports._
import com.pharbers.baseModules.PharbersInjectModule

trait ConnectionInstance extends PharbersInjectModule {
    override val id: String = "mongodb-connect"
    override val configPath: String = "pharbers_config/mongodb_connect.xml"
    override val md = "server_host" :: "server_port" :: "connect_name" :: "connect_pwd" :: "conn_name" :: Nil

    def conn_name: String = config.mc.find(p => p._1 == "conn_name").get._2.toString

    def server_host: String = config.mc.find(p => p._1 == "server_host").get._2.toString

    def server_port: Int = config.mc.find(p => p._1 == "server_port").get._2.toString.toInt

    lazy val addr = new com.mongodb.casbah.Imports.ServerAddress(server_host, server_port)
    lazy val _conn = MongoClient(addr)

    var _conntion: Map[String, MongoCollection] = Map.empty

    def getCollection(coll_name: String): MongoCollection = {
        if (!_conntion.contains(coll_name)) _conntion += (coll_name -> _conn(conn_name)(coll_name))

        _conntion(coll_name)
    }

    def getAllCollectionNames: scala.collection.mutable.Set[String] = _conn(conn_name).getCollectionNames()

    def resetCollection(coll_name: String): Unit = getCollection(coll_name).drop

    def isExisted(coll_name: String): Boolean = getCollection(coll_name).nonEmpty

    def releaseConntions(): Unit = _conntion = Map.empty

}
