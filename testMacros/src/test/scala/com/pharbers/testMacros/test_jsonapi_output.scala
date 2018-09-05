package com.pharbers.testMacros

import io.circe.syntax._
import com.pharbers.macros._
import com.pharbers.testMacros.model.user
import com.pharbers.util.log.phLogTrait
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport

object test_jsonapi_output extends App with CirceJsonapiSupport with phLogTrait {
    val entity = user()
    entity.id = "5b7e454a8fb8076c3c3304c7"

//    val contacter1 = contacter()
//    contacter1.id = "contacter1"
//    val contacter2 = contacter()
//    contacter2.id = "contacter2"
//    contacter2.address =  "contacter_address2"
//    contacter2.mobile =  "contacter_mobile2"
//    contacter2.name = "contacter_郭监护人2"
//    contacter2.nickname = "contacter_nickname2"
//    contacter2.wechatid = "contacter_wechatid2"
//    entity.contacts = Some(contacter1 :: contacter2 :: Nil)
//
//    val guardian1 = guardian()
//    guardian1.id = "guardian1"
//    val guardian2 = guardian()
//    guardian2.id = "guardian1"
//    guardian2.address =  "guardian_address2"
//    guardian2.mobile =  "guardian_mobile2"
//    guardian2.name = "guardian_郭监护人2"
//    guardian2.nickname = "guardian_nickname2"
//    guardian2.wechatid = "guardian_wechatid2"
//    entity.guardians = Some(guardian1 :: guardian2 :: Nil)

    phLog(entity)
    val entity2 = entity.copy()
//    entity2.contacts = Some(contacter1 :: contacter2 :: Nil)
//    entity2.guardians = Some(guardian1 :: guardian2 :: Nil)
//    phLog(entity2)

//    val result = toJsonapi(entity)(new TestJsonapiConvert())
    import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
    val result = toJsonapi(entity)
    phLog(result.asJson)

//    val entity = profile()
//    entity.name = "jeorch"
//    entity.age = 18
//    val l = List("abc", 3, 5)
//    val m1 = Map("key1" -> "value1", "key2" -> "value2")
//    val m2 = Map("key3" -> "value3", "key4" -> "value4")
//    entity.test1 = l
//    entity.test2 = m1
//    entity.test3 = List(m1, m2)
//    entity.test4 = Map("1" -> l, "adlkjaf" -> m2)
//    val c0 = company()
//    val c1 = company()
//    c0.company_name = "法伯科技"
//    c1.company_name = "黑镜子"
//    val o1 = Order()
//    o1.title = "订单1"
//    o1.abc = 1245
//    val o2 = Order()
//    o2.title = "订单2"
//    o2.abc = 6543
//    entity.company = Some(c0)
//    entity.orders = Some(List(o1, o2))
}



