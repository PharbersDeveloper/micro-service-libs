package com.pharbers.models.entity.apm.teacher

import com.pharbers.macros.api.commonEntity
import com.pharbers.models.entity.auth.user
import com.pharbers.macros.common.connecting.One2OneConn

@One2OneConn[user]("student")
@One2OneConn[download_paper]("paper")
class download_layout() extends commonEntity {
    var time: Long = 0L
}
