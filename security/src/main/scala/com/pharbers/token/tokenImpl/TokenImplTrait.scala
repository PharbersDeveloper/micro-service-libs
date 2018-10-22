//package com.pharbers.token.tokenImpl
//
//import java.nio.charset.StandardCharsets
//import it.sauronsoftware.base64.Base64
//
//// import bminjection.encrypt.RSA.RSAEncryptTrait
//import com.pharbers.encrypt.RSA.RSAEncryptTrait
//// import bminjection.token.AuthTokenTrait
//import com.pharbers.token.AuthTokenTrait
//import play.api.libs.json.JsValue
//// import bminjection.encrypt.RSA.RSA_wocao
//import com.pharbers.encrypt.RSA.RSA_wocao
//
///**
//  * Created by alfredyang on 01/06/2017.
//  */
//trait TokenImplTrait extends RSAEncryptTrait with AuthTokenTrait {
//    def encrypt2Token(js : JsValue) : String = {
//        val data = js.toString().getBytes(StandardCharsets.UTF_8)
//        val encodedData = RSA_wocao.encryptByPrivateKey(data, privateKey)
//        Base64.encode(new String(encodedData, StandardCharsets.ISO_8859_1))
//    }
//
//    def decrypt2JsValue(auth_token : String) : JsValue = {
//        val decodedData = RSA_wocao.decryptByPublicKey(Base64.decode(auth_token).getBytes(StandardCharsets.ISO_8859_1), publicKey)
//        val target = new String(decodedData, StandardCharsets.UTF_8)
//        play.api.libs.json.Json.parse(target)
//    }
//}
