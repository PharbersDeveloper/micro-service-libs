package com.pharbers.macros.api

import java.util.UUID

import com.pharbers.jsonapi.model.Error

trait errorEntity extends commonEntity {
    val status: String
    val code: String
    val title: String
    val detail: String

    def toError: Error = {
        Error(
            id = if (id.isEmpty) Some("tmp_" + UUID.randomUUID()) else Some(id),
            status = Some(status), code = Some(code),
            title = Some(title), detail = Some(detail)
        )
    }
}