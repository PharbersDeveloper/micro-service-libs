package com.pharbers.macros.common.connecting

import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradis to expand macro annotations")
final class ToStringMacro extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro ToStringMacro.impl
}

object ToStringMacro {
    def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
        import c.universe._

        val class_tree = annottees.map(_.tree).toList match {
            case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends ..$parents { $self => ..$stats }" :: Nil =>

                val params = paramss.flatMap { params =>
                    val q"..$trees" = q"..$params"
                    trees
                }
                val fields = stats.flatMap { params =>
                    val q"..$trees" = q"..$params"
                    trees.map {
                        case q"$mods def toString(): $tpt = $expr" => q""
                        case x => x
                    }.filter(_ != EmptyTree)
                }
                val total_fields = params ++ fields

                val toStringDefList = total_fields.map {
                    case q"$mods val $tname: $tpt = $expr" => q"""${tname.toString} + " = " + $tname"""
                    case q"$mods var $tname: $tpt = $expr" => q"""${tname.toString} + " = " + $tname"""
                    case _ => q""
                }.filter(_ != EmptyTree)
                val toStringBody = if(toStringDefList.isEmpty) q""" "" """ else toStringDefList.reduce { (a, b) => q"""$a + ", " + $b""" }
                val toStringDef = q"""override def toString(): String = ${tpname.toString()} + "(" + $toStringBody + ")""""

                q"""
                    $mods class $tpname[..$tparams] $ctorMods(...$paramss) extends ..$parents { $self => ..$stats
                        $toStringDef
                    }
                """

            case _ => c.abort(c.enclosingPosition, "Annotation @One2OneConn can be used only with class")
        }

        c.Expr[Any](class_tree)
    }
}
