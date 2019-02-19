package com.pharbers.tools.execl

import com.pharbers.tools.base.phDataHandleTrait
import com.pharbers.tools.execl.handle.phWriteExcelHandle

/**
  * Created by clock on 18-2-27.
  */
trait phWriteExcelTrait extends phDataHandleTrait {
    def writeByList(content: List[Map[String, Any]], output_file: String,
                    sheet: String = "Sheet1", cellNumArg: Map[String, Int] = Map()): Unit = {

        def getWriteSeq(row: Map[String, Any]): Map[String, Int] = {
            var i = -1
            row.map { x =>
                i += 1
                x._1 -> i
            }
        }

        implicit val writeSeq: Map[String, Int] = cellNumArg.size match {
            case 0 => getWriteSeq(content.head)
            case _ => cellNumArg
        }
        phWriteExcelHandle(output_file).write(content, sheet)
    }
}