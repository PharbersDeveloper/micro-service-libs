package com.blackmirror.bmOssDriver

import java.io.File
import java.util

import com.aliyun.oss.OSSClient
import com.aliyun.oss.model._
import com.pharbers.baseModules.PharbersInjectModule

/**
  * Created by jeorch on 18-3-1.
  */
class BmOssClientInstance extends BmOssClientTrait with PharbersInjectModule {

    override val id: String = "oss-config"
    override val configPath: String = "pharbers_config/oss_config.xml"
    override val md = "endpoint":: "accessKeyId":: "accessKeySecret" :: Nil

    def endpoint: String = config.mc.find(p => p._1 == "endpoint").get._2.toString
    def accessKeyId: String = config.mc.find(p => p._1 == "accessKeyId").get._2.toString
    def accessKeySecret: String = config.mc.find(p => p._1 == "accessKeySecret").get._2.toString

    // 创建OSSClient实例
    lazy val _client = new OSSClient(endpoint, accessKeyId, accessKeySecret)


    override def createBucket(bucketName: String): Bucket = {
        _client.createBucket(bucketName)
    }


    override def putObject(bucketName: String, key: String, file: File): PutObjectResult = {
        _client.putObject(bucketName, key, file)
    }


    override def downloadFile2Local(getObjectRequest: GetObjectRequest, file: File): ObjectMetadata = {
        _client.getObject(getObjectRequest, file)
    }

    def getAllBuckets: util.List[Bucket] = {
        _client.listBuckets()
    }

    def deleteBucket(bucketName : String): Unit = {
        _client.deleteBucket(bucketName)
    }

    //列出bucket下所有objects
    def listObjects(bucketName : String): ObjectListing = {
        _client.listObjects(bucketName)
    }

    //批量删除
    def deleteObjects (deleteObjectsRequest: DeleteObjectsRequest): DeleteObjectsResult = {
        _client.deleteObjects(deleteObjectsRequest)
    }

    //设置ACL
    def setBucketAcl (bucketName: String, cannedACL: CannedAccessControlList): Unit = {
        _client.setBucketAcl(bucketName, cannedACL)
    }

    def close : Unit = {
        _client.shutdown()
    }

}
