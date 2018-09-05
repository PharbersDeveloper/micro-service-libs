package com.pharbers.testMacros

import com.pharbers.testMacros.model.{company, user}

object test_conn extends App {

    val c0 = company()
    val c1 = company()
    val u0 = user()
    val u1 = user()
    val u2 = user()

    u0.email = "test_email"
    u0.password = "test_password"
    c0.company_name = "pharbers"
    c0.users = Some(List(u0))
    println(c0)
}
