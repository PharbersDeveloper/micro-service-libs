package com.pharbers.kafka.connect.mongodb.filter

import com.pharbers.TmIOAggregation

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/08/13 12:45
  * @note 一些值得注意的地方
  */
object Aggregation {
    def TmInputAgg(proposalId: String, projectId: String, periodId: String): String ={
        TmIOAggregation.TmInputAgg(proposalId, projectId, periodId)
    }
}
