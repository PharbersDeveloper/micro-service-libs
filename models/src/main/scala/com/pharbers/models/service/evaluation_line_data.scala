package com.pharbers.models.service

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[evaluation_line_intro_data]("intor")
@ToStringMacro
class evaluation_line_data extends commonEntity {
    var report_analysi_val: String = ""         // 报表分析与决策
    var market_insight_val: String = ""         // "市场洞察力"
    var target_setting_val: String = ""         // "目标分级能力"
    var strategy_execution_val: String = ""     // "公司战略执行力"
    var resource_allocation_val: String = ""    // "资源分配与优化"
    var plan_deployment_val: String = ""        // "销售计划部署"
    var leadership_val: String = ""             // "领导力"
}