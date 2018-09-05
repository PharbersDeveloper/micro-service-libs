package com.pharbers.models

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class phase() extends commonEntity {
    var phase: Int = 0
    var name: String = ""
    var report_id: String = ""
    var report_style: String = ""
    var connect_dest: List[Map[String, Any]] = Nil
    var connect_reso: List[Map[String, Any]] = Nil
    var connect_rep: List[Map[String, Any]] = Nil
    var connect_goods: List[Map[String, Any]] = Nil
    var dest_rep: List[Map[String, Any]] = Nil
    var dest_goods: List[Map[String, Any]] = Nil
    var dest_goods_rep: List[Map[String, Any]] = Nil
}