package com.pharbers.kafka.connect.oss.readerV2

import java.io.{File, FileInputStream}
import java.util
import java.util.Scanner
import java.util.concurrent.LinkedBlockingQueue

import collection.JavaConverters._
import com.pharbers.kafka.connect.oss.concurrent.RowData
import com.pharbers.kafka.schema.OssTask
import com.pharbers.util.log.PhLogable
import org.scalatest.{BeforeAndAfterAll, FunSuite}

/** 功能描述
  *
  * @param args 构造参数
  * @tparam T 构造泛型参数
  * @author dcs
  * @version 0.0
  * @since 2020/01/02 10:43
  * @note 一些值得注意的地方
  */
class TestExcelReaderV2 extends FunSuite with BeforeAndAfterAll with PhLogable{
    val task = new OssTask("", "jobId", "traceId", "ossKey", "xlsx", "test", "", "", 0L,
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence](),
        new util.ArrayList[CharSequence]())

    test("test excel reader"){
        val reader = new ExcelReaderV2("test", task)
        val path = "src/test/resources/1575555558843.xlsx"
        val stream = new FileInputStream(new File(path))
        val plate = new LinkedBlockingQueue[RowData]()
        reader.init(stream, "")
        reader.read(plate)
        plate.asScala
                .groupBy(x => x.getJobId)
                .foreach(x => {
                    val rows = x._2.filter(data => data.getType == "SandBox")
                    val count = rows.size
                    val length = x._2.filter(data => data.getType == "SandBox-Length").head.getRow.head
                    logger.debug(s"jobId: ${x._1}, count: $count, length: $length")
                    x._1 match {
                        case "test0" => assert(count == 14 && length == "14")
                        case "test1" => assert(count == 879 && length == "879")
                        case "test2" => assert(count == 20516 && length == "20516")
                        case "test3" => assert(count == 52 && length == "52")
                    }
                    val check = getCheck(x._1)
                    val rowIterator = rows.iterator
                    while (rowIterator.hasNext){
                        val row = rowIterator.next().getRow
                        val checkRow = check.nextLine().replace("\uFEFF", "").replaceAll("\"", "")
                        println(row.mkString(","))
                        println(checkRow)
                        assert(row.mkString(",").equals(checkRow))
                    }
                })
    }

    test("test ExcelReaderForMaxDeliveryData"){
        val reader = new ExcelReaderForMaxDeliveryData("test", task)
        val path = "src/test/resources/Amgen_MAX_data_仅201912.xlsx"
        val stream = new FileInputStream(new File(path))
        val plate = new LinkedBlockingQueue[RowData]()
        reader.init(stream, "")
        reader.read(plate)
        plate.asScala
                .groupBy(x => x.getJobId)
                .foreach(x => {
                    val rows = x._2.filter(data => data.getType == "SandBox")
                    val count = rows.size
                    val length = x._2.filter(data => data.getType == "SandBox-Length").head.getRow.head
                    val schema = x._2.filter(data => data.getType == "SandBox-Schema").head.getRow
                    val schemaCheck = "Year\u001F年,Quarter\u001F季度,Month\u001F月,Hospital Code_Pharbers\u001F法伯医院编码,Hospital Code_CPA\u001FCPA医院编码,Hospital Code_Amgen\u001FAmgen医院编码,Hospital Name\u001F医院名称,Province\u001F省份,City\u001F城市,Hospital Grade\u001F医院等级,City Tier\u001F城市等级,CPA Hospital Flag\u001FCPA医院标记,Area_Amgen\u001FAmgen大区,Target Hospital Flag\u001F目标医院标记,Target City Flag\u001F目标城市标记,PCI\u001FPCI手术量,Value (RMB)\u001FMarket\u001FStatin_total\u001F他汀市场,Value (RMB)\u001FMolecule\u001FAtovastatin\u001F阿托伐他汀,Value (RMB)\u001FMolecule\u001FRosuvastatin\u001F瑞舒伐他汀,Value (RMB)\u001FMolecule\u001FSimvastatin\u001F辛伐他汀,Value (RMB)\u001FMolecule\u001FEzetimibe+Ezetimibe/Simvastatin\u001F依折麦布+依折麦布辛伐他汀,Value (RMB)\u001FMolecule\u001FOther statin\u001F其它他汀,Value (RMB)\u001FBrand\u001FAtovastatin_Lipitor\u001F阿托伐他汀_立普妥,Value (RMB)\u001FBrand\u001FAtovastatin_other brand\u001F阿托伐他汀_其他品牌,Value (RMB)\u001FBrand\u001FRosuvastatin_Crestor\u001F瑞舒伐他汀_可定,Value (RMB)\u001FBrand\u001FRosuvastatin_other brand\u001F瑞舒伐他汀_其他品牌,Value (RMB)\u001FBrand\u001FSimvastatin_Zocor\u001F辛伐他汀_舒降之,Value (RMB)\u001FBrand\u001FSimvastatin_other brand\u001F辛伐他汀_其他品牌,Value (RMB)\u001FBrand\u001FEzetimibe_Ezetrol\u001F依折麦布_益适纯,Value (RMB)\u001FBrand\u001FEzetimibe/Simvastatin_Vytorin\u001F依折麦布辛伐他汀_葆至能,Value (RMB)\u001FBrand\u001FOther statin_all brand\u001F其它他汀_全部品牌,Value (RMB)\u001FMarket\u001FAnti-platelet\u001F抗血小板市场,Value (RMB)\u001FMolecule\u001FClopidogrel\u001F氯吡格雷,Value (RMB)\u001FMolecule\u001FTicagrelor\u001F替格瑞洛,Value (RMB)\u001FBrand\u001FClopidogrel_Plavix\u001F氯吡格雷_波立维,Value (RMB)\u001FBrand\u001FClopidogrel_other brand\u001F氯吡格雷_其他品牌,Value (RMB)\u001FBrand\u001FTicagrelor_Brilinta\u001F替格瑞洛_倍林达,Volume (MoT)\u001FMarket\u001FStatin_total\u001F他汀市场,Volume (MoT)\u001FMolecule\u001FAtovastatin\u001F阿托伐他汀,Volume (MoT)\u001FMolecule\u001FRosuvastatin\u001F瑞舒伐他汀,Volume (MoT)\u001FMolecule\u001FSimvastatin\u001F辛伐他汀,Volume (MoT)\u001FMolecule\u001FEzetimibe+Ezetimibe/Simvastatin\u001F依折麦布+依折麦布辛伐他汀,Volume (MoT)\u001FMolecule\u001FOther statin\u001F其它他汀,Volume (MoT)\u001FBrand\u001FAtovastatin_Lipitor\u001F阿托伐他汀_立普妥,Volume (MoT)\u001FBrand\u001FAtovastatin_other brand\u001F阿托伐他汀_其他品牌,Volume (MoT)\u001FBrand\u001FRosuvastatin_Crestor\u001F瑞舒伐他汀_可定,Volume (MoT)\u001FBrand\u001FRosuvastatin_other brand\u001F瑞舒伐他汀_其他品牌,Volume (MoT)\u001FBrand\u001FSimvastatin_Zocor\u001F辛伐他汀_舒降之,Volume (MoT)\u001FBrand\u001FSimvastatin_other brand\u001F辛伐他汀_其他品牌,Volume (MoT)\u001FBrand\u001FEzetimibe_Ezetrol\u001F依折麦布_益适纯,Volume (MoT)\u001FBrand\u001FEzetimibe/Simvastatin_Vytorin\u001F依折麦布辛伐他汀_葆至能,Volume (MoT)\u001FBrand\u001FOther statin_all brand\u001F其它他汀_全部品牌,Volume (MoT)\u001FMarket\u001FAnti-platelet\u001F抗血小板市场,Volume (MoT)\u001FMolecule\u001FClopidogrel\u001F氯吡格雷,Volume (MoT)\u001FMolecule\u001FTicagrelor\u001F替格瑞洛,Volume (MoT)\u001FBrand\u001FClopidogrel_Plavix\u001F氯吡格雷_波立维,Volume (MoT)\u001FBrand\u001FClopidogrel_other brand\u001F氯吡格雷_其他品牌,Volume (MoT)\u001FBrand\u001FTicagrelor_Brilinta\u001F替格瑞洛_倍林达"
                    assert(schema.mkString(",") == schemaCheck)
                    logger.debug(s"jobId: ${x._1}, count: $count, length: $length")
                    x._1 match {
                        case "test0" => assert(count == 24757 && length == "24757")
                    }
                })

    }

    def getCheck(jobId: String): Scanner = {
        val path = jobId match {
            case "test0" => "src/test/resources/1-3月未到医院名单及说明.csv"
            case "test1" => "src/test/resources/需替换及补充数据.csv"
            case "test2" => "src/test/resources/施贵宝1903.csv"
            case "test3" => "src/test/resources/药品信息更新列表.csv"
        }
        new Scanner(new File(path))
    }

}
