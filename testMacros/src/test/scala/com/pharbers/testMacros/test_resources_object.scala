package com.pharbers.testMacros

import io.circe.syntax._
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.macros.convert.jsonapi._
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model.RootObject.ResourceObject
import com.pharbers.models.entity.auth.user

object test_resources_object extends App with CirceJsonapiSupport with phLogTrait {
    val test_data =
        """
          |{
          |    "data": {
          |        "type": "profile",
          |        "id": "uuida9811cd0-5d7a-47ec-adf2-71eb1655ec31",
          |        "attributes": {
          |            "name": "jeorch",
          |            "age": 18,
          |            "test1": [
          |                "abc",
          |                3,
          |                5
          |            ],
          |            "test2": {
          |                "key1": "value1",
          |                "key2": "value2"
          |            },
          |            "test3": [
          |                {
          |                    "key1": "value1",
          |                    "key2": "value2"
          |                },
          |                {
          |                    "key3": "value3",
          |                    "key4": "value4"
          |                }
          |            ],
          |            "test4": {
          |                "1": [
          |                    "abc",
          |                    3,
          |                    5
          |                ],
          |                "adlkjaf": {
          |                    "key3": "value3",
          |                    "key4": "value4"
          |                }
          |            }
          |        },
          |        "relationships": {
          |            "orders": {
          |                "data": [
          |                    {
          |                        "type": "Order",
          |                        "id": "001"
          |                    }
          |                ]
          |            },
          |            "company": {
          |                "data": {
          |                    "type": "company",
          |                    "id": "002"
          |                }
          |            }
          |        }
          |    },
          |    "included": [
          |    {
          |        "type": "profile",
          |        "id": "tmp_5fc1c513-2fd1-4683-89a0-103db91769e6",
          |        "attributes": {
          |            "name": "99999",
          |            "age": 99,
          |            "test1": [],
          |            "test2": {},
          |            "test3": [],
          |            "test4": {}
          |        }
          |    },
          |    {
          |        "type": "Order",
          |        "id": "001",
          |        "attributes": {
          |            "title": "订单1",
          |            "abc": 1245
          |        }
          |    },
          |    {
          |        "type": "Order",
          |        "id": "tmp_adcb632a-9f10-4b33-8143-f7af75a97014",
          |        "attributes": {
          |            "title": "订单2",
          |            "abc": 6543
          |        }
          |    },
          |    {
          |        "type": "company",
          |        "id": "002",
          |        "attributes": {
          |            "company_name": "法伯科技"
          |        },
          |        "relationships": {
          |            "users": {
          |                "data": [
          |                    {
          |                        "type": "profile",
          |                        "id": "tmp_5fc1c513-2fd1-4683-89a0-103db91769e6"
          |                    }
          |                ]
          |            }
          |        }
          |    }
          |]
          |}
        """.stripMargin
    val json_data = parseJson(test_data)
    val jsonapi = decodeJson[RootObject](json_data)
//    phLog(jsonapi)

    val resources = jsonapi.data.get.asInstanceOf[ResourceObject]
    val included = jsonapi.included
//    phLog(resources)
//    phLog(included)


//    val entity = fromResourceObject(resources, included)(new TestResourceConvert())
    import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._
    val entity = fromResourceObject[user](resources, included)


//    val entity = profile()
//    val entity2 = profile()
//    entity.name = "jeorch"
//    entity.age = 18
//    entity2.name = "99999"
//    entity2.age = 99
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
//    c0.users = Some(List(entity2))
//    val o1 = Order()
//    o1.title = "订单1"
//    o1.abc = 1245
//    val o2 = Order()
//    o2.title = "订单2"
//    o2.abc = 6543
//    entity.company = Some(c0)
//    entity.orders = Some(List(o1, o2))


    println(entity)


//    val result = toResourceObject(entity)(new TestResourceConvert())
    val result = toResourceObject(entity)
    println(result)
    println(result.asJson)
}