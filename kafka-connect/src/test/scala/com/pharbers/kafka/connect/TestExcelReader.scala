package com.pharbers.kafka.connect

import java.io.File

import com.monitorjbl.xlsx.StreamingReader

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2019/11/26 16:40
  * @note 一些值得注意的地方
  */
object TestExcelReader extends App {
    val reader = StreamingReader.builder().open(new File("C:\\Users\\EDZ\\Desktop\\1574303123720.xlsx"))
    val cells = reader.getSheetAt(0).rowIterator().next().cellIterator()
    while (cells.hasNext){
        val cell = cells.next()
        val value = cell.getStringCellValue
        println(value)
    }
}
