package com.pharbers.pactions.excel.input

import com.pharbers.excel.common.xls.PhExcelXLSInterface
import com.pharbers.excel.common.xls.PhExcelXLSInterface.datarow

object PhExcelXLS {

    def apply(filePath : String, sheetIndex : Int) : PhExcelXLS = {
        new PhExcelXLS().startParse(filePath, sheetIndex)
    }

    def apply(filePath : String, sheetName : String) : PhExcelXLS = {
        new PhExcelXLS().startParse(filePath, sheetName)
    }
}

class PhExcelXLS extends PhExcelXLSInterface {
    implicit val tmp = this
    implicit val p2r : javafx.util.Pair[java.lang.Integer, java.lang.Integer] => Range = (p => Range(p.getKey, p.getValue))
    implicit def toTuple(p : javafx.util.Pair[java.lang.Integer, java.lang.Integer]) : (Int, Int) = (p.getKey, p.getValue)

    var cur_row : Option[datarow] = None
    var rows_start : Int = 0
    var rows_end : Int = 0

    def currentIndex : Int = cur_row.map (cp => cp.queryCurrentRowIndex().asInstanceOf[Int]).getOrElse(0)

    def startParse(filePath : String, sheetIndex : Int) : PhExcelXLS = {
        val workbook = openExcelFile(filePath)
        val sheet = openExcelSheet(sheetIndex, workbook)
        val (start, end) : (Int, Int) = rowsInSheet(sheet)
        rows_start = start
        rows_end = end
        cur_row = Some(queryRowByIndex(sheet, rows_start))
        this
    }

    def startParse(filePath : String, sheetName : String) : PhExcelXLS = {
        val workbook = openExcelFile(filePath)
        val sheet = openExcelSheet(sheetName, workbook)
        val (start, end) : (Int, Int) = rowsInSheet(sheet)
        rows_start = start
        rows_end = end
        cur_row = Some(queryRowByIndex(sheet, rows_start))
        this
    }

    def currentRowData : List[String] = cur_row.map { cr =>

        def dataForIndex(index : Int) : String = queryCellByIndex(cr, index).queryCellString
        def dataAcc(lst : List[Int]) : List[String] = if (lst.isEmpty) Nil
                                                      else dataForIndex(lst.head) :: dataAcc(lst.tail)

        dataAcc(colsInRow(cr).toList)

    }.getOrElse(throw new Exception("没有数据了"))

    def isValidataRow(row : Int): Boolean = row <= rows_end && row >= 0

    def hasNextRow: Boolean = cur_row.exists { cr =>
        val row = cr.queryCurrentRowIndex();

        val cur = row + 1
        isValidataRow(cur)
    }

    def nextRow = cur_row.map { cr =>
        val row = cr.queryCurrentRowIndex();
        val sheet = cr.queryWorksheet();

        val cur = row + 1
        if (isValidataRow(cur)) {
            cur_row = Some(queryRowByIndex(sheet, cur))

        } else cur_row = None

    }.getOrElse(throw new Exception("初始化未完成"))
}
