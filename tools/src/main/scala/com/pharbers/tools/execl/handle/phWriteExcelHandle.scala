package com.pharbers.tools.execl.handle

import java.io.FileOutputStream
import scala.collection.immutable.Map
import org.apache.poi.ss.usermodel.Sheet
import org.xml.sax.helpers.DefaultHandler
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
  * Created by clock on 17-9-7.
  */
case class phWriteExcelHandle(output_file: String) extends DefaultHandler {
    def write(content: List[Map[String, Any]], sheetName: String = "Sheet1")(implicit cellNumMap: Map[String, Int]): Unit = {
        val wb = new XSSFWorkbook()
        val sheet = wb.createSheet()
        wb.setSheetName(0, sheetName)
        writeSheet(content, sheet)
        val os = new FileOutputStream(output_file)
        wb.write(os)
        os.close()
    }

    def writeSheet(content: List[Map[String, Any]], sheet: Sheet)(implicit cellNumMap: Map[String, Int]): Unit = {
        if(content == Nil)
            throw new Exception("data parse error => Write data is null")
        writeTitle(content.head.keys.toList, sheet)
        for(i <- 1 to content.length){
            val rowRef = sheet.createRow(i)
            content(i-1).foreach { c =>
                val cell = rowRef.createCell(cellNumMap(c._1))
                c._2 match {
                    case i: Int => cell.setCellValue(i)
                    case l: Long => cell.setCellValue(l)
                    case f: Float => cell.setCellValue(f)
                    case d: Double => cell.setCellValue(d)
                    case s: String => cell.setCellValue(s)
                    case _ => cell.setCellValue(c._2.asInstanceOf[String])
                }
            }
        }

    }

    def writeTitle(title: List[String], sheet: Sheet)(implicit cellNumMap: Map[String, Int]): Unit = {
        val row = sheet.createRow(0)
        if(title.diff(cellNumMap.keys.toList) != Nil)
            throw new Exception("data parse error => Column mapping error")
        cellNumMap.foreach{x =>
            row.createCell(x._2).setCellValue(x._1)
        }
    }
}
