package com.pharbers.macros.convert.mongodb

import scala.reflect.ClassTag
import com.mongodb.casbah.Imports._
import scala.collection.JavaConversions._
import scala.reflect.runtime.{universe => ru}

trait dbutil {

    /** 判断是否为关联到实体的one属性 **/
    private[this] def isConnOneInject(f: ru.Symbol): Boolean =
        f.info <:< ru.typeOf[Option[_]] && f.info.typeArgs.length == 1 &&
                f.info.typeArgs.head.baseClasses.map(_.name.toString).contains("commonEntity")

    /** 判断是否为关联到实体的many属性 **/
    private[this] def isConnManyInject(f: ru.Symbol): Boolean =
        f.info <:< ru.typeOf[Option[List[_]]] && f.info.typeArgs.length == 1 &&
                f.info.typeArgs.head.typeArgs.head.baseClasses.map(_.name.toString).contains("commonEntity")

    def attrValue(field: ru.Symbol, dbo: DBObject): Any = {

        def recursive(data: Any): Any = {
            data match {
                case t: DBObject =>
                    val res = t.toMap.toMap.map { x =>
                        val result = x._2 match {
                            case i: BasicDBList => i.toList.map(recursive)
                            case i: DBObject => recursive(i)
                            case _: Any => x._2
                        }
                        Map(x._1 -> result)
                    }.toList
                    val result = (Map[Any, Any]() /: res) (_ ++ _)
                    result

                case t: MongoDBList =>
                    val res = t.toList.map {
                        case i: BasicDBList => recursive(i)
                        case i: DBObject => recursive(i)
                        case i: Any => i
                    }
                    res

                case d: Any => d
            }
        }

        field.info.typeSymbol.name.toString match {

            case "Int" | "Double" | "Long" | "Float" =>
                dbo.getAs[Number](field.name.toString.trim).get

            case "Boolean" =>
                dbo.getAs[Boolean](field.name.toString.trim).get

            case "String" =>
                dbo.getAs[String](field.name.toString.trim).get

            case "List" =>
                val t = dbo.getAs[MongoDBList](field.name.toString.trim).get
                recursive(t)

            case "Map" =>
                val t = dbo.getAs[DBObject](field.name.toString.trim).get
                recursive(t)

            case "Option" =>
               field.info.typeArgs.head.typeSymbol.name.toString match {
                    case "List" =>
                        val t = dbo.getAs[MongoDBList](field.name.toString.trim).get
                        Some(recursive(t))

                    case _ =>
                        val t = dbo.getAs[DBObject](field.name.toString.trim).get
                        Some(recursive(t))
                }

            case _ => ???
        }
    }

    def DBObjectBindObject(dbo: Option[DBObject], className: String): Any = {
        val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
        val class_symbol = runtime_mirror.classSymbol(Class.forName(className))
        val class_mirror = runtime_mirror.reflectClass(class_symbol)
        val class_fields = class_symbol.typeSignature.members
                    .filter(p => p.isTerm && !p.isMethod)
                    .filter(!isConnOneInject(_))
                    .filter(!isConnManyInject(_))
                    .toList
        val constructors = class_symbol.typeSignature.members.filter(_.isConstructor).toList
        val constructorMirror = class_mirror.reflectConstructor(constructors.head.asMethod)
        val model = constructorMirror()
        val inst_mirror = runtime_mirror.reflect(model)
        val commonIdTerm = inst_mirror.symbol.typeSignature.member(ru.TermName("id")).asTerm
        val idMirror = inst_mirror.reflectField(commonIdTerm)
        idMirror.set(dbo.get.getAs[ObjectId]("_id").getOrElse("-1").toString)
        class_fields.foreach { field =>
            val field_name = field.name.toString.trim
            val field_symbol = inst_mirror.symbol.typeSignature.member(ru.TermName(field_name)).asTerm
            val field_mirror = inst_mirror.reflectField(field_symbol)
            val t = attrValue(field, dbo.get)
            field_mirror.set(t)
        }
        model
    }

    def loadJsonApiType[T: ClassTag](model: T): String = {
        val class_ = model.asInstanceOf[T]
        val runtime_mirror = ru.runtimeMirror(class_.getClass.getClassLoader)
        val inst_mirror = runtime_mirror.reflect(model)
        val typeTerm = inst_mirror.symbol.typeSignature.member(ru.TermName("type")).asTerm
        val typeMirror = inst_mirror.reflectField(typeTerm)
        typeMirror.get.toString
    }

    def Struct2DBObject[T: ClassTag](model: T): DBObject = {
        val class_ = model.asInstanceOf[T]
        val runtime_mirror = ru.runtimeMirror(class_.getClass.getClassLoader)
        val inst_mirror = runtime_mirror.reflect(model)
        val inst_symbol = inst_mirror.symbol
        val class_symbol = inst_symbol.typeSignature
        val class_fields = inst_symbol.typeSignature.members
                .filter(p => p.isTerm && !p.isMethod)
                .filter(!isConnOneInject(_))
                .filter(!isConnManyInject(_))
                .toList

        var dbo = DBObject()
        class_fields.foreach { field =>
            val field_name = field.name.toString.trim
            val field_symbol = class_symbol.member(ru.TermName(field_name)).asTerm
            val field_mirror = inst_mirror.reflectField(field_symbol)
            dbo ++= DBObject(field_name -> field_mirror.get)
        }
        dbo
    }
}
