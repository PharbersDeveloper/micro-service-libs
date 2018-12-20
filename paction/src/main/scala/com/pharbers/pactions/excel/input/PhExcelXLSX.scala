package com.pharbers.pactions.excel.input

import com.pharbers.excel.common.xlsx.PhExcelXLSXInterface
import com.pharbers.excel.common.xlsx.PhExcelXLSXInterface.datarow

object PhExcelXLSX {

    def apply(filePath : String, sheetIndex : Int) : PhExcelXLSX = {
        new PhExcelXLSX().startParse(filePath, sheetIndex)
    }

    def apply(filePath : String, sheetName : String) : PhExcelXLSX = {
        new PhExcelXLSX().startParse(filePath, sheetName)
    }
}

class PhExcelXLSX extends PhExcelXLSXInterface {
    implicit val tmp: PhExcelXLSX = this
    implicit val p2r: javafx.util.Pair[java.lang.Integer, java.lang.Integer] => Range = p => Range(p.getKey, p.getValue)
    implicit def toTuple(p : javafx.util.Pair[java.lang.Integer, java.lang.Integer]) : (Int, Int) = (p.getKey, p.getValue)

    var cur_row : Option[datarow] = null

    def currentIndex : Int = cur_row.map (cp => cp.queryCurrentRowIndex().asInstanceOf[Int]).getOrElse(0)

    def startParse(filePath : String, sheetIndex : Int) : PhExcelXLSX = {
        val workbook = openExcelFile(filePath)
        val sheet = openExcelSheet(sheetIndex, workbook)
        cur_row = Some(sheet.queryFirstRow())
        this
    }

    def startParse(filePath : String, sheetName : String) : PhExcelXLSX = {
        val workbook = openExcelFile(filePath)
        val sheet = openExcelSheet(sheetName, workbook)
        cur_row = Some(sheet.queryFirstRow())
        this
    }

    def currentRowData : List[String] = cur_row.map { cr =>

        def dataForIndex(index : Int) : String = queryCellByIndex(cr, index).queryCellString
        def dataAcc(lst : List[Int]) : List[String] = if (lst.isEmpty) Nil
                                                      else dataForIndex(lst.head) :: dataAcc(lst.tail)

        dataAcc(colsInRow(cr).toList)

    }.getOrElse(throw new Exception("没有数据了"))

    def isValidataRow(row : Int) = true

    def hasNextRow : Boolean = cur_row.exists { cr =>
        cr.queryWorksheet().hasNexRow.asInstanceOf[scala.Boolean]
    }

    def nextRow(): Unit = {
        if (this.hasNextRow) {
            cur_row.map { cr =>
                val tmp = cr.queryWorksheet().queryNextRow()
                if (tmp == null) cur_row = None
                else cur_row = Some(tmp)

            }.getOrElse(throw new Exception("初始化未完成"))
        } else cur_row = None
    }
}
