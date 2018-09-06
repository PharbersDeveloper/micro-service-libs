/**
 * For Database Query
 * Created By Alfred Yang
 */

package com.pharbers.mongodbConnect

import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.pharbers.baseModules.PharbersInjectModule

trait connection_instance extends PharbersInjectModule {

    override val id: String = "mongodb-connect"
    override val configPath: String = "pharbers_config/mongodb_connect.xml"
    override val md = "server_host" :: "server_port" :: "connect_name" :: "connect_pwd" :: "conn_name" :: Nil

    def conn_name : String = config.mc.find(p => p._1 == "conn_name").get._2.toString
    def server_host : String = config.mc.find(p => p._1 == "server_host").get._2.toString
    def server_port : Int = config.mc.find(p => p._1 == "server_port").get._2.toString.toInt

    // val addr = new com.mongodb.casbah.Imports.ServerAddress("localhost", 2017)
    lazy val addr = new com.mongodb.casbah.Imports.ServerAddress(server_host, server_port)
    //	val credentialsList = MongoCredential.createPlainCredential("dongdamaster", conn_name, "dongda@master".toCharArray)
    //    val _conn = MongoClient(addr) //, List(credentialsList))
    lazy val _conn = MongoClient(addr)

    var _conntion : Map[String, MongoCollection] = Map.empty

    def getCollection(coll_name : String) : MongoCollection = {
        if (!_conntion.contains(coll_name)) _conntion += (coll_name -> _conn(conn_name)(coll_name))

        _conntion.get(coll_name).get
    }

	def getAllCollectionNames: scala.collection.mutable.Set[String] = _conn(conn_name).getCollectionNames()

    def resetCollection(coll_name : String) : Unit = getCollection(coll_name).drop

    def isExisted(coll_name : String) : Boolean = !(getCollection(coll_name).isEmpty)

    def releaseConntions = _conntion = Map.empty
}

trait IDatabaseContext {
	var coll_name : String = null

	protected def openConnection(implicit dc : connection_instance) : MongoCollection =
//	  	_data_connection._conn(_data_connection.conn_name)(coll_name)
        dc._conn(dc.conn_name)(coll_name)
	protected def closeConnection = null
}

class ALINQ[T] {
	var w : T => Boolean = x => true
	var ls : List[T] = Nil
  
	def in(l: List[T]) : ALINQ[T] = {
		ls = l
		this
	}

	def where(f: T => Boolean) : ALINQ[T] = {
		w = f
		this
	}
	
	def select[U](cr: (T) => U) : IQueryable[U] = {
		var nc = new Linq_List[U]
		for (i <- ls) {
			if (w(i)) nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def contains : Boolean = {
		for (i <- ls) {
			if (w(i)) true
		}
		false
	}
	
	def count : Int = ls.count(w)
}

object from {
	def apply[T]() : ALINQ[T] = new ALINQ[T]
	def db()(implicit dc : connection_instance) : AMongoDBLINQ = new AMongoDBLINQ(dc)

    /**
      * Map reduce
      */
    def mapReduce(coll : String, m : String, r : String, q : Option[DBObject], out : String)(implicit dc : connection_instance) : Boolean = {

        val cmd =
        MapReduceCommand(
            coll,
            m,
            r,
            out,
            q,
            finalizeFunction = None,
            verbose = true
        )

//        val a = _data_connection.getCollection(coll).mapReduce(cmd)
        val a = dc.getCollection(coll).mapReduce(cmd)
        true
    }
}

class AMongoDBLINQ(val dc : connection_instance) extends IDatabaseContext {
	var w : DBObject = null
	var w2 : List[DBObject] = List.empty
  
	def in(l: String) : AMongoDBLINQ = {
		coll_name = l
		this
	}

	def where(args: Any* ) : AMongoDBLINQ = {
		w = new MongoDBObject
		for (arg <- args) {
			arg match {
			  case a: (String, AnyRef) => w += a
			  case a: DBObject => w = w ++ a
			  case _ => w
			}
		}
		this
	}
	
	def select[U](cr: (MongoDBObject) => U)(implicit dc : connection_instance) : IQueryable[U] = {
	 
		val mongoColl = openConnection
		val ct = mongoColl.find(w)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}

	def contains(implicit dc : connection_instance) : Boolean = {
		!(select (x => x).empty)
	}
	
	def selectTop[U](n : Int)(o : String)(cr : (MongoDBObject) => U)(implicit dc : connection_instance) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).sort(MongoDBObject(o -> -1)).limit(n)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}

	def selectSkipTopWithoutSort[U](skip : Int)(take : Int)(cr : (MongoDBObject) => U)(implicit dc : connection_instance) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).skip(skip).limit(take)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def selectSkipTop[U](skip : Int)(take : Int)(o : String)(cr : (MongoDBObject) => U)(implicit dc : connection_instance) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).sort(MongoDBObject(o -> -1)).skip(skip).limit(take)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def selectSkipTopLoc[U](skip : Int)(take : Int)(cr : (MongoDBObject) => U)(implicit dc : connection_instance) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).skip(skip).limit(take)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}

	def selectCursor(implicit dc : connection_instance) : MongoCursor = openConnection.find(w)

    /**
      * TODO: 后期需要优化
      */
    def aggregate(group : MongoDBObject) : DBObject = {
        val pipeline = MongoDBList(MongoDBObject("$match" -> w)) ++
                        MongoDBList(MongoDBObject("$group" -> group))

//        val a = _data_connection._conn(_data_connection.conn_name)
        val a = dc._conn(dc.conn_name)
        a.command(MongoDBObject("aggregate" -> coll_name, "pipeline" -> pipeline))
    }

	def count(implicit dc : connection_instance) : Int = openConnection.count(w)
	
	
	// TODO: 老版本的Client项目与Calc项目中用到以下方法，带新版本成熟后，删除改fun
	def selectOneByOne[U](o: String)(cr: (MongoDBObject) => U)(implicit dc: connection_instance): MongoCursor = {
		val mongoColl = openConnection
		mongoColl.find(w).sort(MongoDBObject(o -> -1))
	}
	
	def selectAggregate[U](cr: (MongoDBObject) => U)(implicit dc: connection_instance) : IQueryable[U] = {
		val mongoColl = openConnection
		val aggregationOptions = AggregationOptions(AggregationOptions.CURSOR)
		val ct = mongoColl.aggregate(w2, aggregationOptions)
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
	
	def selectSort[U](o : String)(cr: (MongoDBObject) => U)(implicit dc: connection_instance) : IQueryable[U] = {
		val mongoColl = openConnection
		val ct = mongoColl.find(w).sort(MongoDBObject(o -> 1))
		var nc = new Linq_List[U]
		for (i <- ct) {
			nc = (nc :+ cr(i)).asInstanceOf[Linq_List[U]]
		}
		nc
	}
}
