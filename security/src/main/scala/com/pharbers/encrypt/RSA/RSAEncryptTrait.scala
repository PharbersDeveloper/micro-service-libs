package com.pharbers.encrypt.RSA

import com.pharbers.encrypt.EncryptTrait
import com.pharbers.mongodbDriver.MongoDB._data_connection
import com.pharbers.mongodbConnect.from
import com.mongodb.casbah.Imports._

/**
  * Created by alfredyang on 01/06/2017.
  */
trait RSAEncryptTrait extends javaEncryptTrait with EncryptTrait {
    implicit val dc = _data_connection
    lazy val queryKeys: (String, String) = {
//        (from db() in "encrypt_config" where ("project" -> "Dongda") select (x => x)).toList match {
        (from db() in db_key where ("project" -> project) select (x => x)).toList match {
            case head :: Nil => {
                (head.getAs[String]("public_key").get, head.getAs[String]("private_key").get)
            }
            case Nil => {
                val keyMap = RSAUtils.genKeyPair
                val pub_key = RSAUtils.getPublicKey(keyMap)
                val pri_key = RSAUtils.getPrivateKey(keyMap)

                val builder = MongoDBObject.newBuilder
                builder += "project" -> project //"Dongda"
                builder += "public_key" -> pub_key
                builder += "private_key" -> pri_key

//                _data_connection.getCollection("encrypt_config") += builder.result
                dc.getCollection(db_key) += builder.result

                (pub_key, pri_key)
            }
        }
    }

    override lazy val publicKey: String = queryKeys._1
    override lazy val privateKey: String = queryKeys._2
}
