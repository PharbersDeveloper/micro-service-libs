//import java.io.File
//
//import com.aliyun.oss.model.{CannedAccessControlList, DeleteObjectsRequest, GetObjectRequest}
//import org.scalatest.FunSuite
//import com.blackmirror.bmOssDriver.BmOssClientInstance
//
//import scala.collection.JavaConversions._
///**
//  * Created by jeorch on 18-3-1.
//  */
//class BmOssDriverSuite extends FunSuite {
//
//    val ossClient = new BmOssClientInstance()
//    val bucketName = "aha123"
//    val ossDir = "test/"
//    val localDir = "upload"
//
//    test("create bucket") {
//        val bucket = ossClient.createBucket(bucketName)
//        println(s"bucket:${bucket.getName}")
//    }
//
//    test("upload file") {
//        val dir = new File(localDir)
//        for (file <- dir.listFiles()){
//            println(file.getName)
//            ossClient.putObject(bucketName, dir + file.getName, file)
//        }
//    }
//
//
//    test ("download file") {
//        ossClient.downloadFile2Local(new GetObjectRequest(bucketName, ossDir + "logo.PNG"), new File(localDir+"/1.jpg"))
//    }
//
//    test("delete bucket") {
//        ossClient.deleteBucket("aha123")
//    }
//
//    test("delete buckets by filter") {
//        val buckets = ossClient.getAllBuckets
//        buckets.filterNot(x => x.getName == "blackmirror").map { x =>
//            val bucket = x.getName
//            println(s"start delete $bucket")
////            ossClient.setBucketAcl(bucket, CannedAccessControlList.Private)
//            val keys = ossClient.listObjects(bucket).getObjectSummaries.map(x => x.getKey).toList
//            if (keys.size != 0) ossClient.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(keys))
//            ossClient.deleteBucket(bucket)
//            println(s"success delete $bucket")
//        }
//        ossClient.close
//    }
//
//    test("close oss client"){
//        ossClient.close
//    }
//
//}
