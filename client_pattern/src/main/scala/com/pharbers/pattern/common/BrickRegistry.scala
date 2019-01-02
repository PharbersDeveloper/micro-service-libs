package com.pharbers.pattern.common

import com.pharbers.driver.PhRedisDriver

case class BrickRegistry() {
    def registryRoute(route: String): List[String] = new PhRedisDriver().getListAllValue(route)
}
