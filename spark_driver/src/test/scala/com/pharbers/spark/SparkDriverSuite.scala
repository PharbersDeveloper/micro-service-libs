package com.pharbers.spark

import com.pharbers.spark.util.mongo2RDD

/**
  * Created by clock on 18-2-26.
  */
object SparkDriverSuite extends App {

    def testMongo2Rdd(): Unit = {
        val driver = phSparkDriver("test_driver")
        import driver._

        val mongodbHost = "192.168.100.174"
        val mongodbPort = "27017"
        def users = driver.setUtil(mongo2RDD()).mongo2RDD(mongodbHost, mongodbPort, "baby_time_test", "users")

        println(users)
    }
    testMongo2Rdd()

//    test("Test read csv") {
//        val driver = phSparkDriver("test_driver")
//        val file_path = "/home/jeorch/work/max_file/Max/a95e32b2-9945-4bd2-8b42-32271b19259a60b738de-0125-4959-a883-63c3e8c3d5d4"
//
//        val rdd = driver.csv2RDD(file_path, delimiter = 31.toChar.toString)
////        rdd.foreach(x => println(x))
//        rdd.printSchema()
////        rdd.show(false)
////        println(rdd.count())
//    }
//
//    test("spark read mongo") {
//        val sd = phSparkDriver("test_driver")
//        val mongoRDD = sd.mongo2RDD("127.0.0.1", "27017", "Max_Test", "Allelock_Factorized_Units&Sales_WITH_OT1712")
//        val mongoDF = mongoRDD.toDF()
//        mongoDF.show(10)
//    }
//
//    test("spark write mongo") {
//        val databaseName = "Max_Test"
//        val sd = phSparkDriver("test_driver")
//
//        val mongoRDD = sd.mongo2RDD("127.0.0.1", "27017", "Max_Test", "Allelock_Factorized_Units&Sales_WITH_OT1712")
//        val mongoDF = mongoRDD.toDF()
//
//        sd.dataFrame2Mongo(mongoDF, mongodbHost, mongodbPort, databaseName, "testColl", "append")
//    }
//
//    test("spark parallel") {
//
////        val sd = phSparkDriver("job-id-0")
////        val maxDF = sd.readCsv("hdfs:///workData/Max/4b2b7d6d-ba4a-4ce2-8f88-5ba8bfeeaf7ed9f80401-9944-41ca-b4c1-c362f85457b0", 31.toChar.toString)
////        maxDF.coalesce(1).write.save("hdfs:///client/" + getUUID(0))
//
//        val t1 = new Thread() {
//            override def run(): Unit = {
//                println("T1")
//                val sd1 = phSparkDriver("job-id-1")
//                println("get ==== S1")
//                val maxDF = sd1.readCsv("hdfs:///workData/Max/4b2b7d6d-ba4a-4ce2-8f88-5ba8bfeeaf7ed9f80401-9944-41ca-b4c1-c362f85457b0", 31.toChar.toString)
//                maxDF.coalesce(1).write.save("hdfs:///client/" + getUUID(1))
//                sd1.stopCurrConn
//                println("T1 done")
//            }
//        }
//        val t2 = new Thread() {
//            override def run(): Unit = {
//                println("T2")
//                val sd2 = phSparkDriver("job-id-2")
//                println("get ==== S2")
//                val maxDF = sd2.readCsv("hdfs:///workData/Max/4b2b7d6d-ba4a-4ce2-8f88-5ba8bfeeaf7ed9f80401-9944-41ca-b4c1-c362f85457b0", 31.toChar.toString)
//                maxDF.coalesce(1).write.save("hdfs:///client/" + getUUID(2))
//                sd2.stopCurrConn
//                println("T2 done")
//            }
//        }
//        val t3 = new Thread() {
//            override def run(): Unit = {
//                println("T3")
//                //TODO:相同的job_id
//                val sd2 = phSparkDriver("job-id-1")
//                println("get ==== S3")
//                val maxDF = sd2.readCsv("hdfs:///workData/Max/4b2b7d6d-ba4a-4ce2-8f88-5ba8bfeeaf7ed9f80401-9944-41ca-b4c1-c362f85457b0", 31.toChar.toString)
//                maxDF.coalesce(1).write.save("hdfs:///client/" + getUUID(3))
//                sd2.stopCurrConn
//                println("T3 done")
//            }
//        }
//        t1.start()
//        t2.start()
////        t3.start()
//
//        Thread.sleep(70 * 1000)
//        phSparkDriver("test_driver").ss.stop()
//        println("All done")
//
//    }
//
//    def getUUID(index: Int): String = {
//        index.toString + "@" + UUID.randomUUID().toString
//    }

}
