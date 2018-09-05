package com.pharbers.jsonapi.json.circe

import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import spray.http.ContentTypes
import spray.http.MediaTypes._
import spray.httpx.marshalling.Marshaller
import com.pharbers.jsonapi.model.RootObject
import spray.httpx.unmarshalling.Unmarshaller

trait CirceJsonapiSupport extends CirceJsonapiEncoders with CirceJsonapiDecoders {
    implicit val circeJsonapiMarshaller = Marshaller.delegate[RootObject, String](
        `application/vnd.api+json`,
        `application/json`,
        ContentTypes.`application/json`
    )(_.asJson.noSpaces)
    implicit val circeJsonapiUnmarshaller = Unmarshaller.delegate[String, RootObject](
        `application/vnd.api+json`,
        `application/json`
    )(decode[RootObject](_).right.get)

    protected def parseJson(jsonString: String): Json = parse(jsonString).right.get

    protected def decodeJson[T](json: Json)(implicit d: io.circe.Decoder[T]): T = json.as[T].right.get
}

object CirceJsonapiSupport extends CirceJsonapiSupport
