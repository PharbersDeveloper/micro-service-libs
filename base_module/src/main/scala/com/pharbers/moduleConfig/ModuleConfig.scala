package com.pharbers.moduleConfig

import java.io.File

import com.pharbers.baseModules.PharbersModule

import scala.xml.Node
import scala.xml.XML

object ModuleConfig {
    def apply : ModuleConfig = ???

    implicit val f : (ConfigDefines, Node) => ConfigImpl = { (c, n) =>
        ConfigImpl(
            c.md map { x => (n \ x).toList match {
                case head :: _ => (x -> (head \\ "@value").toString)
                case Nil => ???
            }
        })}
    implicit val fr : (String, String) => Node = { (path, id) =>
        ((XML.loadFile(new File(path))) \\ id).toList match {
            case head :: _ => head
            case Nil => ???
        }
    }
}

trait ModuleConfig extends ConfigDefines { this : PharbersModule =>
    def loadConfig(path : String)
                  (implicit f : (ConfigDefines, Node) => ConfigImpl,
                   fr : (String, String) => Node) : ConfigImpl = f(this, fr(path, id))
}
