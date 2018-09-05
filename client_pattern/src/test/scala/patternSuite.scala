//
//import com.pharbers.Person
//import io.circe.Decoder.Result
//import org.scalatest.FunSuite
//import org.zalando.jsonapi.json.circe.{CirceJsonapiDecoders, CirceJsonapiEncoders}
//import org.zalando.jsonapi.model.JsonApiObject.StringValue
//import org.zalando.jsonapi.model.RootObject.ResourceObject
//import org.zalando.jsonapi.model.{Attribute, Links, RootObject}
//import org.zalando.jsonapi.{JsonapiRootObjectWriter, _}
//
////import org.zalando.jsonapi.json.playjson.PlayJsonJsonapiFormat
////import org.zalando.jsonapi.json.sprayjson.SprayJsonJsonapiProtocol
////import org.zalando.jsonapi.model.JsonApiObject.StringValue
////import org.zalando.jsonapi.model.{Attribute, Links, RootObject}
////import org.zalando.jsonapi.model.RootObject.ResourceObject
////import spray.json._
//
//import org.zalando.jsonapi.json.playjson.PlayJsonJsonapiSupport._
//import play.api.libs.json._
//import play.api.libs.json.Json
//
//import io.circe._
//import io.circe.generic.auto._
//import io.circe.parser._
//import io.circe.syntax._
//
///**
//  * Created by alfredyang on 07/07/2017.
//  */
////class patternSuite extends FunSuite with SprayJsonJsonapiProtocol {
//class patternSuite extends FunSuite with CirceJsonapiEncoders with CirceJsonapiDecoders {
//
//    protected def parseJson(jsonString: String): Json = parse(jsonString).right.get
//    protected def decodeJson[T](json: Json)(implicit d: io.circe.Decoder[T]): T = json.as[T].right.get
//
//    implicit val personJsonapiRootObjectWriter: JsonapiRootObjectWriter[Person] = new JsonapiRootObjectWriter[Person] {
//        override def toJsonapi(person: Person) = {
//            RootObject(data = Some(ResourceObject(
//                `type` = "person",
//                id = Some(person.id.toString),
//                attributes = Some(List(
//                    Attribute("name", StringValue(person.name))
//                )), links = Some(List(Links.Self("http://test.link/person/42", None))))))
//        }
//    }
//
//    com.pharbers.test("pharbers pattern com.pharbers.test") {
//        val p = Person(42, "Alfred")//.asJson
//        println(p.getClass)
//        println(p)
//        println(p.asJson)
//
//        val tt = p.rootObject.asJson
////        val a = p.rootObject
//        println(tt.getClass)
//        println(tt)
//
//
//
//        val rr = decodeJson[RootObject](tt)
//        println(rr.getClass)
//        println(rr)
//    }
//}