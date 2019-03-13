package com.pharbers.tools.csv

import java.io.{FileWriter, RandomAccessFile}

import com.pharbers.tools.base.phDataHandleTrait

import scala.collection.immutable.Map

/**
  * Created by clock on 18-2-28.
  */
trait phWriteCsvTrait extends phDataHandleTrait {

    def appendByLine(line: Map[String, Any], output_file: String, delimiter: String)
                    (implicit titleSeqArg: List[String] = Nil): Unit = {

        if (line.isEmpty) throw new Exception("写入的数据为空")
        val titleSeq = if (titleSeqArg.isEmpty) line.keys.toList else titleSeqArg
        val out = new RandomAccessFile(getFile(output_file), "rw")
        val temp = titleSeq.map(t => line(t).toString).mkString(delimiter) + chl
        val lineStr = if(out.length == 0) titleSeq.mkString(delimiter) + chl + temp else temp

        out.seek(out.length)
        out.write(lineStr.getBytes)
        out.close()
    }

    def writeByList(content: List[Map[String, Any]], output_file: String, delimiter: String)
                   (implicit titleSeqArg: List[String] = Nil): Unit = {

        if (content.isEmpty) throw new Exception("写入的数据为空")
        val titleSeq = if (titleSeqArg.isEmpty) content.head.keys.toList else titleSeqArg
        val out = new FileWriter(getFile(output_file))

        out.write(titleSeq.mkString(delimiter) + chl)
        content.foreach { m =>
            val line = titleSeq.map( t => m(t).toString ).mkString(delimiter) + chl
            out.write(line)
        }
        out.close()
    }
}
