package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@ToStringMacro
class hospmedicinfo() extends commonEntity {
    var pre_target: Long = 0L
    var total_budget: Long = 0L
    var prod_category: String = ""
    var prod_name: String = ""
    var overview: List[Map[String, Any]] = Nil
    var history: Map[String, List[Map[String, Any]]] = Map(
        "columns" -> List(
            Map("label" -> "时间", "valuePath" -> "time", "align" -> "center", "sortable" -> false, "sorted" -> true, "minResizeWidth" -> "70px"),
            Map("label" -> "负责代表", "valuePath" -> "rep_name", "align" -> "left", "sortable" -> false),
            Map("label" -> "时间分配(天)", "valuePath" -> "use_day", "align" -> "left", "sortable" -> false),
            Map("label" -> "预算(元)/比例", "valuePath" -> "use_budget", "align" -> "left", "sortable" -> false),
            Map("label" -> "指标/增长/达成率", "valuePath" -> "target", "align" -> "left", "sortable" -> false)
        ),
        "columnsValue" -> Nil
    )
    var detail: Map[String, List[Map[String, Any]]] = Map(
        "columns" -> List(
            Map("label" -> "商品名", "valuePath" -> "prod_name", "align" -> "center", "sortable" -> false),
            Map("label" -> "上市时间", "valuePath" -> "launch_time", "align" -> "center"),
            Map("label" -> "医保类型", "valuePath" -> "insure_type", "align" -> "center"),
            Map("label" -> "研发类型", "valuePath" -> "research_type", "align" -> "center"),
            Map("label" -> "公司考核价", "valuePath" -> "ref_price", "align" -> "center")
        ),
        "columnsValue" -> Nil
    )
}