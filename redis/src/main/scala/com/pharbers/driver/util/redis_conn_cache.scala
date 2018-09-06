package com.pharbers.driver.util

/**
  * Created by jeorch on 17-12-14.
  */
trait redis_conn_cache {
    implicit val conn_instance = redis_conn_obj
}
