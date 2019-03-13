package com.pharbers.tools.facade

import scala.collection.immutable.Map
import com.pharbers.tools.csv.{phCsvFileInfo, phHandleCsv}
import com.pharbers.tools.execl.{phExcelFileInfo, phHandleExcel}

/**
  * @description:
  * @author: clock
  * @date: 2019-02-19 10:59
  */
case class phExcel2Csv() {
    def excel2Csv(excelInfo: phExcelFileInfo, csvInfo: phCsvFileInfo)
                 (implicit filterFun: Map[String, String] => Boolean = _ => true,
                  postFun: Map[String, String] => Option[Map[String, String]] = tr => Some(tr)): Unit = {
        val excelHandle = phHandleExcel()
        val csvHandle = phHandleCsv()
        var title: Map[String, String] = Map()

        val processFun: Map[String, String] => Unit = { row =>
            if (title.isEmpty) title = row.map{ x => (x._1, excelInfo.fieldArg.getOrElse(x._2, x._2))}
            else {
                val temp = title.map { x => (x._2, row.getOrElse(x._1, "")) }
                val data = temp.map { x =>
                    if (x._2 == "") (x._1, excelHandle.setDefaultValue(x._1, temp, excelInfo))
                    else x
                }
                if(filterFun(data)){
                    csvHandle.appendByLine(
                        postFun(data).getOrElse(throw new Exception("data parse error => postFun error")),
                        csvInfo.file_local, csvInfo.delimiter)
                }
            }
        }

        excelHandle.callbackReading(excelInfo)(processFun)
    }
}
