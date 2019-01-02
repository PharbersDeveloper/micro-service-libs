package com.pharbers.macros.common.connecting

import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradis to expand macro annotations")
class One2ManyConn[C](param_name: String) extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro One2ManyConn.impl
}

object One2ManyConn {
    def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
        import c.universe._

        val class_tree = annottees.map(_.tree).toList match {
            case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends commonEntity[..$ptpname] with ..$parents { $self => ..$stats }" :: Nil =>

                val (conn_type, conn_name) = c.prefix.tree match {
                    case q"new One2ManyConn[$conn_type]($conn_name)" =>
                        (conn_type.toString, conn_name.toString.replace("\"", ""))
                    case _ => c.abort(c.enclosingPosition, "Annotation @One2ManyConn must provide conn_type and conn_name !")
                }

                val params = paramss.flatMap { params =>
                    val q"..$trees" = q"..$params"
                    trees.map {
                        case q"$mods val $tname: $tpt = $expr" =>
                            q"private[this] var $tname: $tpt = $expr"

                        case q"$mods var $tname: $tpt = $expr" =>
                            q"private[this] var $tname: $tpt = $expr"
                    }
                }
                val fields = stats.flatMap { params =>
                    val q"..$trees" = q"..$params"
                    trees.map {
                        case q"$mods val $tname: $tpt = $expr" =>
                            q"$mods var $tname: $tpt = $expr"
                        case q"$mods var $tname: $tpt = $expr" =>
                            q"$mods var $tname: $tpt = $expr"
                        case x => x
                    }.filter(_ != EmptyTree)
                }
                val conn_many_var = q"var ${TermName(conn_name)}: Option[List[${TypeName(conn_type)}]] = None"
                val conn_fields = params ++ fields ++ Seq(conn_many_var)

                val conn_many_def = q"""
                    private[this] def ${TermName("jsonapi_to_" + conn_name)}(rd: Option[RootObject.Data], included: Option[Included]): Option[List[${TypeName(conn_type)}]] = {
                        rd match {
                            case Some(reos: ResourceObjects) => Some(reos.array.map(x => fromResourceObject[${TypeName(conn_type)}](x, included)(ResourceReaderMaterialize)).toList)
                            case _ => None
                        }
                    }

                    private[this] def ${TermName(conn_name + "_to_jsonapi")}(obj: Option[List[${TypeName(conn_type)}]]): (Option[RootObject.Data], Option[Included]) = {
                        obj match {
                            case Some(entitys: List[_]) =>
                                val reos_includeds = entitys.map(toResourceObject[${TypeName(conn_type)}](_)(ResourceReaderMaterialize))
                                val reos = reos_includeds.map(_._1)
                                val includeds = Included(ResourceObjects(reos_includeds.map(_._2).flatMap(_.resourceObjects.array).distinct))
                                (Some(ResourceObjects(reos)), Some(includeds))
                            case _ => (None, None)
                        }
                    }
                """

                q"""{
                    $mods class $tpname[..$tparams] $ctorMods() extends commonEntity[..$ptpname] with ..$parents { $self =>
                        ..$conn_fields

                        import com.pharbers.jsonapi.model.Included
                        import com.pharbers.macros.convert.jsonapi._
                        import com.pharbers.jsonapi.model.RootObject
                        import com.pharbers.jsonapi.model.RootObject.ResourceObjects
                        import com.pharbers.macros.convert.jsonapi.ResourceObjectReader.ResourceReaderMaterialize
                        ..$conn_many_def
                    }
                }"""

            case _ => c.abort(c.enclosingPosition, "Annotation @One2ManyConn can be used only with class")
        }

        c.Expr[Any](class_tree)
    }
}
