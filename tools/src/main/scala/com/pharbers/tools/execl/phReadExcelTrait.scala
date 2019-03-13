package com.pharbers.tools.execl

import scala.collection.immutable.Map
import com.pharbers.tools.base.phDataHandleTrait
import com.pharbers.tools.execl.handle.phReadExcelHandle

/**
  * Created by clock on 18-2-27.
  */
trait phReadExcelTrait extends phDataHandleTrait {
    def getCount(excel: phExcelFileInfo): Int = phReadExcelHandle(excel.file_local).getCount

    def getSheetNames(excel: phExcelFileInfo): Seq[String] = phReadExcelHandle(excel.file_local).getSheetNames

    def read2Lst(excel: phExcelFileInfo)
                (implicit filterFun: Map[String, String] => Boolean = _ => true,
                 postFun: Map[String, String] => Option[Map[String, String]] = tr => Some(tr)): List[Map[String, String]] = {

        var title: Map[String, String] = Map()
        var result: List[Map[String, String]] = Nil

        val processFun: Map[String, String] => Unit = { row =>
            if (title.isEmpty) title = row.map{ x => (x._1, excel.fieldArg.getOrElse(x._2, x._2))}
            else {
                val temp = title.map { x => (x._2, row.getOrElse(x._1, "")) }
                val data = temp.map { x =>
                    if (x._2 == "") (x._1, setDefaultValue(x._1, temp, excel))
                    else x
                }
                if(filterFun(data)){
                    result = result :+ postFun(data).getOrElse(throw new Exception("data parse error => postFun error"))
                }
            }
        }

        callbackReading(excel)(processFun)
        result
    }

    def callbackReading(excel: phExcelFileInfo)(processFun: Map[String, String] => Unit): Unit = {
        phReadExcelHandle(excel.file_local, processFun).process(excel.sheetId, excel.sheetName)
    }

    def setDefaultValue(key: String, row: Map[String, String], excel: phExcelFileInfo): String = {

        def getValue(targetCell: String): String = {
            row.get(targetCell) match {
                case Some(s) if s == "" => setDefaultValue(targetCell, row, excel)
                case Some(s) => s
                case None => ""
            }
        }

        excel.defaultValueArg.get(key) match {
            case Some(s) if s.startsWith("$") => getValue(s.tail)
            case Some(s) => s
            case None => ""
        }
    }
}