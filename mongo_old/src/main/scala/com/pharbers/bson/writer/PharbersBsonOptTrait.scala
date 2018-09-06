package com.pharbers.bson.writer

import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.bson.writer.fop.BsonFlushTrait
import org.bson.{BSONObject, BasicBSONObject}

trait BsonFlushMemoryTrait extends PharbersInjectModule {
    override val id: String = "bson-flush-memory"
    override val configPath: String = "pharbers_config/bson_flush_memory.xml"
    override val md = "buffer-size" :: Nil

    val buffer_size = config.mc.find(p => p._1 == "buffer-size").get._2.toString.toInt

    case class BsonflushImpl(override val path : String,
                             override val bufferSize : Int) extends BsonFlushTrait

    val path : String
    lazy val fl = BsonflushImpl(path, buffer_size)

    def appendObject(o : BSONObject) = fl.appendBsonObject(o)
    def close = fl.closeFlush

    def map2bson(map : Map[String, Any]) : BSONObject = {
        val bson : BSONObject = new BasicBSONObject()
        map.foreach(x => bson.put(x._1, x._2))
        bson
    }
}

case class bsonFlushMemory(override val path : String) extends BsonFlushMemoryTrait


