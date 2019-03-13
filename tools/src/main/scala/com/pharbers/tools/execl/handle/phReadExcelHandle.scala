package com.pharbers.tools.execl.handle

import java.io.InputStream
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xssf.model.SharedStringsTable
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.xml.sax.{Attributes, InputSource, XMLReader}
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.xml.sax.helpers.{DefaultHandler, XMLReaderFactory}
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator

/**
  * Created by clock on 18-2-27.
  */
case class phReadExcelHandle(file_local: String,
                             processFun: Map[String, String] => Unit = _ => Unit) extends DefaultHandler {

    private val pkg = OPCPackage.open(file_local)
    private val fr = new XSSFReader(pkg)
    private val sst: SharedStringsTable = fr.getSharedStringsTable

    protected var rowMap: Map[String,String] = Map()
    private var phIsOpen = false
    private var ref: String = ""
    private var lastContents = ""
    private var phNextIsNull = false
    private var phNextIsString = false
    private var phNextIsNumber = false

    val getCount: Int = {
        val xs = fr.getSheetsData.asInstanceOf[XSSFReader.SheetIterator]

        var count = 0
        while(xs.hasNext){
            count += 1
            xs.next()
        }
        count
    }

    lazy val getSheetNames: Seq[String] = {
        val xs = fr.getSheetsData.asInstanceOf[XSSFReader.SheetIterator]
        var sheetNames = Seq[String]()
        while (xs.hasNext) {
            xs.next()
            sheetNames = sheetNames :+ xs.getSheetName
        }
        sheetNames
    }

    def process(sheetId: Int = 1, sheetName: String = ""): Unit = {
        val parser: XMLReader = fetchSheetParser(sst)

        val index = if (sheetName != "") getSheetIndex(fr.getSheetsData.asInstanceOf[XSSFReader.SheetIterator], sheetName)
                    else sheetId

        //读取指定sheet
        val sheet: InputStream  = fr.getSheet("rId" + index)
        val sheetSource = new InputSource(sheet)
        parser.parse(sheetSource)
        sheet.close()
    }

    private def fetchSheetParser(sst: SharedStringsTable) = {
        val parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser")
        parser.setContentHandler(this)
        parser
    }

    private def getSheetIndex(xs: SheetIterator, sheetName: String): Int = {
        var index = 0
        var flag = 0
        while(xs.hasNext && flag == 0){
            xs.next()
            if(xs.getSheetName == sheetName) flag = 1
            else index += 1
        }

        if(index == 0)
            throw new Exception("data parse error => not found sheet name")
        else
            index += 1

        index
    }

    override def startElement(uri: String, localName: String, name: String, attr: Attributes): Unit = {
        if ("inlineStr".equals(name) || "v".equals(name)) {
            phIsOpen = true
        }else if (name.equals("c")) {// c => 单元格
            // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
            val cellType = attr.getValue("t")

            //单元格是否为空
            if (cellType == null) {
                phNextIsNull = true
            } else {
                phNextIsNull = false
            }

            if(cellType != null && "s".equals(cellType)) phNextIsString = true
            else phNextIsString = false

            //改变当前指向单元格
            ref = attr.getValue("r")
        }
        // 置空
        lastContents = ""
    }

    private def getPrefix = ref.split("\\d").head

    override def endElement(uri: String, localName: String, name: String): Unit = {
        // 根据SST的索引值的到单元格的真正要存储的字符串
        // 这时characters()方法可能会被调用多次
        // 存在以0开头的字符串
        if (phNextIsString && name == "v") {
            try {
                val idx = Integer.parseInt(lastContents)
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString
                if(lastContents.startsWith("0") && !lastContents.startsWith("0."))
                    lastContents = lastContents.tail
            } catch {
                case _: Exception => Unit
            }
        }

        if(phNextIsNull && lastContents != "") phNextIsNumber = true

        name match {
            case "v" => rowMap += getPrefix -> lastContents.trim
            case "c" if phNextIsNumber => rowMap += getPrefix -> lastContents.trim
            case "c" if phNextIsNull => rowMap += getPrefix -> ""
            case "c" => Unit
            case "row" if rowMap.nonEmpty =>
                processFun(rowMap)
                rowMap = Map()
                ref = null
            case _ => Unit
        }
    }

    override def characters(ch: Array[Char], start: Int, length: Int): Unit = {
        if(phIsOpen) lastContents += new String(ch, start, length)
    }
}
