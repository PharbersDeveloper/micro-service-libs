package com.pharbers.macros.api

case class errorEntity(id: String = "",
                       status: String = "",
                       code: String = "",
                       title: String = "",
                       detail: String = "")