//package com.pharbers.encrypt
//
//import com.pharbers.baseModules.PharbersInjectModule
//
///**
//  * Created by alfredyang on 01/06/2017.
//  * 鉴于RSA的为非对称加密，在加密的过程中的安全性最高，所有优先使用
//  * 如果以后RSA在解密效能上产生了重大问题，可以考虑是用DES加密token
//  */
//trait EncryptTrait extends PharbersInjectModule {
//    // RSA
//    val publicKey : String
//    val privateKey : String
//
//    // DES
//    val desKey : String = "what ever a key" // 随便一个什么key
//
//    override val id: String = "encrypt-trait"
//    override val configPath : String = "pharbers_config/encrypt_trait.xml"
//    override val md: List[String] = "db_key" :: "project" :: Nil
//
//    lazy val db_key : String = config.mc.find(p => p._1 == "db_key").get._2.toString
//    lazy val project : String = config.mc.find(p => p._1 == "project").get._2.toString
//}
