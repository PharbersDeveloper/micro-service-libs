package com.pharbers.pattern

case class BrickRegistry() {
    private val routes: Map[String, List[String]] = Map(
        "/api/v1/login/" -> List("127.0.0.33", "127.0.0.1"),
        "/api/v1/proposal/" -> List("127.0.0.1"),
        "/api/v1/layout/" -> List("127.0.0.1")
    )
    def registryRoute(route: String): List[String] = routes(route)
}
