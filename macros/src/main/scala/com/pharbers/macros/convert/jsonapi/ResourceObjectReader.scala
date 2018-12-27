package com.pharbers.macros.convert.jsonapi


import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import com.pharbers.jsonapi.model.Included
import com.pharbers.jsonapi.model.RootObject.ResourceObject

trait ResourceObjectReader[T] {
    def fromResourceObject(resource: ResourceObject, included: Option[Included]): T

    def toResourceObject(obj: T): (ResourceObject, Included)
}

object ResourceObjectReader {
    implicit def ResourceReaderMaterialize[T]: ResourceObjectReader[T] = macro impl[T]

    def impl[T](c: whitebox.Context)(ttag: c.WeakTypeTag[T]): c.Expr[ResourceObjectReader[T]] = {
        import c.universe._

        val t_symbol = ttag.tpe match {
            case TypeRef(_, str, _) => str
        }
        val t_name = t_symbol.asClass.name.toString
        val t_type = TypeName(t_name)
        val c_name = TypeName(c.freshName("eval$"))

        val q"..$clsdef" = q"""{
        class $c_name extends ResourceObjectReader[$t_type] {

            import java.util.UUID
            import com.pharbers.jsonapi.model._
            import scala.reflect.runtime.{universe => ru}
            import com.pharbers.jsonapi.model.RootObject._
            import com.pharbers.jsonapi.model.JsonApiObject._
            import com.pharbers.jsonapi.model.implicits.JsonApiObjectValueConversions

            /** 基础类型 转 Jsonapi Value */
            implicit def convertAnyToValue: Any => Value = JsonApiObjectValueConversions.convertAnyToValue

            /** Jsonapi Value 转 基础类型 */
            implicit def convertValueToAny: JsonApiObject.Value => Any = {
                case StringValue(str) => str
                case NumberValue(number) if number.isValidInt => number.toInt
                case NumberValue(number) if number.isValidLong => number.toLong
                case NumberValue(number) if number.isBinaryFloat => number.toFloat
                case NumberValue(number) if number.isBinaryDouble => number.toDouble
                case NumberValue(number) => number.toDouble
                case BooleanValue(number) => number
                case JsObjectValue(obj) => obj.map(x => x.name -> convertValueToAny(x.value)).toMap
                case JsArrayValue(arr) => arr.map(x => convertValueToAny(x)).toList
                case NullValue => null
            }

            override def fromResourceObject(resource: ResourceObject, included: Option[Included]): $t_type = {
                val entity_type = ru.typeOf[$t_type]
                val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
                val class_symbol = entity_type.typeSymbol.asClass
                val class_mirror = runtime_mirror.reflectClass(class_symbol)
                val ctor_symbol = entity_type.decl(ru.termNames.CONSTRUCTOR).asMethod
                val ctor_mirror = class_mirror.reflectConstructor(ctor_symbol)
                val entity = ctor_mirror().asInstanceOf[$t_type]

                entity.id = resource.id.get
                entity.`type` = resource.`type`

                /** 解析attributes 到基础数据 **/
                val attrs = resource.attributes.get.toList
                val inst_mirror = runtime_mirror.reflect(entity)
                attrs.foreach { attr =>
                    try {
                        val field_symbol = entity_type.decl(ru.TermName(attr.name)).asTerm
                        val field_mirror = inst_mirror.reflectField(field_symbol)
                        field_mirror.set(convertValueToAny(attr.value))
                    } catch {
                        case _: scala.ScalaReflectionException =>
                            println("not found member \"" + attr.name + "\" in " + ${"\"" + t_name + "\""})
                            Unit
                    }
                }

                /** 根据relationships 找到关联的 includeds **/
                val relationships = resource.relationships
                val expandInfo = relationships match {
                    case Some(relp) =>
                        val includeds = included.get.resourceObjects.array
                        relp.map { case (k, v) =>
                            val tmp = v.data match {
                                case Some(reo: ResourceObject) =>
                                    Some(includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
                                            .getOrElse(throw new Exception(s"not found " + reo.id + "&" + reo.`type` + " in includeds")))
                                case Some(reos: ResourceObjects) =>
                                    Some(ResourceObjects(reos.array.map { reo =>
                                        includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
                                                .getOrElse(throw new Exception(s"not found " + reo.id + "&" + reo.`type` + " in includeds"))
                                    }))
                                case None => None
                            }
                            k -> tmp
                        }
                    case None => Map().empty
                }

                /** 将展开的 relationships 赋值到实体中 **/
                expandInfo.foreach { case (k, v) =>
                    try {
                        val field_symbol = entity_type.member(ru.TermName(k)).asTerm
                        val field_mirror = inst_mirror.reflectField(field_symbol)
                        val extract_symbol = entity_type.member(ru.TermName("jsonapi_to_" + k)).asMethod
                        val extract_mirror = inst_mirror.reflectMethod(extract_symbol)
                        field_mirror.set(extract_mirror(v, included))
                    } catch {
                        case _: scala.ScalaReflectionException =>
                            println("not found connected member \"" + k + "\" in " + $t_name)
                        case _: java.lang.reflect.InvocationTargetException =>
                            println("unable to parse to \"" + $t_name + "\" connected \"" + k + "\" , param = " + v)
                    }
                }

                entity
            }

            override def toResourceObject(obj: $t_type): (ResourceObject, Included) = {
                val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
                val inst_mirror = runtime_mirror.reflect(obj)
                val inst_symbol = inst_mirror.symbol
                val class_symbol = inst_symbol.typeSignature
                val class_field = class_symbol.members.filter(p => p.isTerm && !p.isMethod).toList

                /** 判断是否为关联到实体的one属性 **/
                def isConnOneInject(field_type: ru.Symbol): Boolean =
                    field_type.info <:< ru.typeOf[Option[_]] && field_type.info.typeArgs.length == 1 &&
                            field_type.info.typeArgs.head.baseClasses.map(_.name.toString).contains("commonEntity")

                /** 判断是否为关联到实体的many属性 **/
                def isConnManyInject(field_type: ru.Symbol): Boolean =
                    field_type.info <:< ru.typeOf[Option[List[_]]] && field_type.info.typeArgs.length == 1 &&
                            field_type.info.typeArgs.head.typeArgs.head.baseClasses.map(_.name.toString).contains("commonEntity")

                /** 解析基础信息 */
                val attrs = class_field.filter(!isConnOneInject(_)).filter(!isConnManyInject(_))
                        .map { field_symbol =>
                            val attr_mirror = inst_mirror.reflectField(field_symbol.asTerm)
                            Attribute(field_symbol.name.toString.trim, attr_mirror.get)
                        }.filterNot(NullValue == _.value).asInstanceOf[Attributes]

                /** 解析关联属性的信息 **/
                val conn_data = class_field.filter(f => isConnOneInject(f) || isConnManyInject(f))
                        .map { field =>
                            val field_name = field.name.toString.trim
                            val field_symbol = class_symbol.member(ru.TermName(field_name)).asTerm
                            val field_mirror = inst_mirror.reflectField(field_symbol)
                            val def_symbol = class_symbol.member(ru.TermName(field_name + "_to_jsonapi")).asMethod
                            val def_mirror = inst_mirror.reflectMethod(def_symbol)
                            field_name -> def_mirror(field_mirror.get).asInstanceOf[(Option[RootObject.Data], Option[Included])]
                        }.filter(x => x._2._1.isDefined && x._2._2.isDefined)

                /** 阉割关联属性,只保留 id 和 type 存放到 relationships 中 **/
                val relationships = {
                    conn_data.map(x => x._1 -> x._2._1).map { case (k, v) =>
                        val tmp = v match {
                            case Some(reo: ResourceObject) =>
                                Relationship(data =
                                        Some(ResourceObject(
                                            `type` = reo.`type`,
                                            id = reo.id
                                        ))
                                )
                            case Some(reos: ResourceObjects) =>
                                Relationship(data =
                                        Some(ResourceObjects(
                                            reos.array.map(reo => ResourceObject(
                                                `type` = reo.`type`,
                                                id = reo.id
                                            )).distinct
                                        ))
                                )
                            case None => Relationship()
                        }
                        k -> tmp
                    }.toMap
                }

                /** 将关联属性存到 included 中 **/
                val included = Included(ResourceObjects{
                    (conn_data.flatMap(_._2._2.get.resourceObjects.array) ++
                            conn_data.map(x => x._1 -> x._2._1).flatMap { case (_, v) =>
                        v match {
                            case Some(reo: ResourceObject) => Seq(reo)
                            case Some(reos: ResourceObjects) => reos.array
                            case None => Seq()
                        }
                    }).foldLeft(List.empty[ResourceObject]){
                        (result, cur) => if(result.exists(x => x.id == cur.id && x.`type` == cur.`type`)) result else result :+ cur
                    }
                })

                (ResourceObject(
                    id = if (obj.id.isEmpty) Some("tmp_" + UUID.randomUUID()) else Some(obj.id),
                    `type` = inst_symbol.name.toString,
                    attributes = Some(
                        attrs.toList
                    ),
                    relationships = if (conn_data.isEmpty) None else Some(relationships)
                ), included)
            }
        }
        }"""

        val reVal =q""" new $c_name """

        c.Expr[ResourceObjectReader[T]](Block(clsdef.toList.asInstanceOf[List[c.universe.Tree]], reVal))
    }
}
