package com.blackmirror.bmOssDriver

import java.util
import java.io.File
import com.aliyun.oss.model._

/**
  * Created by jeorch on 18-3-1.
  */
trait BmOssClientTrait {

    def createBucket(bucketName: String): Bucket

    def putObject(bucketName: String, key: String, file: File): PutObjectResult

    def downloadFile2Local(getObjectRequest: GetObjectRequest, file: File): ObjectMetadata

    def getAllBuckets: util.List[Bucket]

    def deleteBucket(bucketName: String): Unit

    //列出bucket下所有objects
    def listObjects(bucketName: String): ObjectListing

    //批量删除
    def deleteObjects (deleteObjectsRequest: DeleteObjectsRequest): DeleteObjectsResult

    //设置ACL
    def setBucketAcl (bucketName: String, cannedACL: CannedAccessControlList): Unit

    def close : Unit

}
