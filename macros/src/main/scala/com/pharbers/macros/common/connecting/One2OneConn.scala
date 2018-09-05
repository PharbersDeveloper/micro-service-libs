package com.pharbers.macros.common.connecting

import scala.reflect.macros.whitebox
import com.pharbers.util.log.phLogTrait
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradis to expand macro annotations")
class One2OneConn[C](param_name: String) extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro One2OneConn.impl
}

object One2OneConn extends phLogTrait {
    def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
        import c.universe._

        val class_tree = annottees.map(_.tree).toList match {
            case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends commonEntity[..$ptpname] with ..$parents { $self => ..$stats }" :: Nil =>
//                phLog("mods = " + mods)
//                phLog("tpname = " + tpname)
//                phLog("tparams = " + tparams)
//                phLog("ctorMods = " + ctorMods)
//                phLog("paramss = " + paramss)
//                phLog("parents = " + parents)
//                phLog("self = " + self)
//                phLog("stats = " + stats)

                val (conn_type, conn_name) = c.prefix.tree match {
                    case q"new One2OneConn[$conn_type]($conn_name)" =>
                        (conn_type.toString, conn_name.toString.replace("\"", ""))
                    case _ => c.abort(c.enclosingPosition, "Annotation @One2OneConn must provide conn_type and conn_name !")
                }
//                phLog("conn_name = " + conn_name)
//                phLog("conn_type = " + conn_type)

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
                val conn_one_var = q"var ${TermName(conn_name)}: Option[${TypeName(conn_type)}] = None"
                val conn_fields = params ++ fields ++ Seq(conn_one_var)
//                phLog("conn_fields = " + conn_fields)

                val conn_one_def = q"""
                    private[this] def ${TermName("jsonapi_to_" + conn_name)}(rd: Option[RootObject.Data], included: Option[Included]): Option[${TypeName(conn_type)}] = {
                        rd match {
                            case Some(reo: ResourceObject) => Some(fromResourceObject[${TypeName(conn_type)}](reo, included)(ResourceReaderMaterialize))
                            case _ => None
                        }
                    }

                    private[this] def ${TermName(conn_name + "_to_jsonapi")}(obj: Option[${TypeName(conn_type)}]): (Option[RootObject.Data], Option[Included]) = {
                        obj match {
                            case Some(entity: ${TypeName(conn_type)}) =>
                                val reo_included = toResourceObject[${TypeName(conn_type)}](entity)(ResourceReaderMaterialize)
                                (Some(reo_included._1), Some(reo_included._2))
                            case _ => (None, None)
                        }
                    }
                """
//                phLog("conn_one_def = " + conn_one_def)


                q"""{
                    $mods class $tpname[..$tparams] $ctorMods() extends commonEntity[..$ptpname] with ..$parents { $self =>
                        ..$conn_fields

                        import com.pharbers.jsonapi.model.Included
                        import com.pharbers.macros.convert.jsonapi._
                        import com.pharbers.jsonapi.model.RootObject
                        import com.pharbers.jsonapi.model.RootObject.ResourceObject
                        import com.pharbers.macros.convert.jsonapi.ResourceObjectReader.ResourceReaderMaterialize
                        ..$conn_one_def
                    }
                }"""

            case _ => c.abort(c.enclosingPosition, "Annotation @One2OneConn can be used only with class")
        }

        c.Expr[Any](class_tree)
    }
}