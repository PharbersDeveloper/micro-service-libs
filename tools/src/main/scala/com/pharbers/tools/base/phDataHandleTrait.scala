package com.pharbers.tools.base

import java.io.File

/**
  * Created by clock on 18-2-27.
  */
trait phDataHandleTrait {
    val spl: String = 31.toChar.toString
    val comma = ","
    val chl = "\n"

    def getFile(file_name: String): File = {
        val file = new File(file_name)

        if(!file.getParentFile.exists()) {
            if(!file.getParentFile.mkdirs()) {
                throw new Exception("创建目标所在目录失败！")
            }
        }
        if(!file.exists())
            file.createNewFile

        file
    }
}
