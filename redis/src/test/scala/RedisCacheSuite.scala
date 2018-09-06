//import com.pharbers.driver.PhRedisDriver
//import com.pharbers.driver.redis.phRedisDriver
//import org.scalatest.FunSuite
//import redis.clients.jedis.Jedis
//
///**
//  * Created by jeorch on 17-12-13.
//  */
//class RedisCacheSuite extends FunSuite {
//    val redisDriver = phRedisDriver().commonDriver
//    test("Test Redis sadd"){
//        val phSetDriver = phRedisDriver().phSetDriver
//        1 to 1000 foreach { i =>
//            phSetDriver.sadd("segment", s"${i}String")
//        }
//    }
//
//    test("Test Read Redis sadd"){
//        val phSetDriver = phRedisDriver().phSetDriver
//
//        val result = phSetDriver.smembers("segment")
//        println(s"result.size = ${result.size}")
//        result.foreach(x => println(s"&& = ${x}"))
//    }
//
//    test("Test Delete Redis sadd"){
//        val redisDriver = phRedisDriver().commonDriver
//        redisDriver.del("segment")
//    }
//
//    test("Test hget nil"){
//        val redisDriver = phRedisDriver().commonDriver
//        val test = redisDriver.hget("0","0")
//        println(s"test=>${test}")
//    }
//
//    test("Test redis max concurrent times => test1") {
//        1 to 1000000 foreach(x => redisDriver.hset("test", s"key${x}", x))
//    }
//
//    test("Test redis max concurrent times => test2") {
//        1 to 1000000 foreach { x =>
//            redisDriver.hset("test", s"key${x + 1000000}", x + 1000000)
//        }
//    }
//
//    test("Test redis max concurrent times => test3") {
//        1 to 1000000 foreach { x =>
//            redisDriver.hset("test", s"key${x + 2000000}", x + 2000000)
//        }
//    }
//
//    test("New Redis Interface test") {
//        val redis_driver = new PhRedisDriver()
//        1 to 1000000 foreach { x =>
//            redis_driver.addString(s"key${x}", s"${x}")
//        }
//    }
//
//    test("New Redis Interface addSet") {
//        val redis_driver = new PhRedisDriver()
//        1 to 10 foreach { x =>
//            redis_driver.addSet("ts",x)
//        }
//    }
//
//}
