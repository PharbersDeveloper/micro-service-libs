package com.pharbers.testMacros

import io.circe.syntax._
import com.pharbers.macros._
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.models.entity.user

object test_jsonapi_input extends App with CirceJsonapiSupport with phLogTrait {
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
    phLog(jsonapi)

//    val entity = formJsonapi(jsonapi)(new TestJsonapiConvert())
    import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
    val entity = formJsonapi[user](jsonapi)
    phLog(entity)


//    val result = toJsonapi(List(entity, entity))(new TestJsonapiConvert())
    val result = toJsonapi(List(entity, entity))
    phLog(result)
    phLog(result.asJson)
}
