package com.pharbers.pactions.excel.input

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.NullWritable
import com.pharbers.pactions.excel.input.multisheet._
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.pharbers.excel.format.input.writable.phExcelWritable
import com.pharbers.excel.format.input.reader.common.PhExcelXLSXReader
import org.apache.hadoop.mapreduce.{InputSplit, JobContext, RecordReader, TaskAttemptContext}

import scala.reflect.ClassTag

class PhXlsxMultiSheetFormat[T <: PhMultiSheet : ClassTag] extends FileInputFormat[NullWritable, phExcelWritable] {

    override def isSplitable(context: JobContext, filename: Path) : Boolean = false

    override def createRecordReader(inputSplit : InputSplit,
                                    taskAttemptContext: TaskAttemptContext) : RecordReader[NullWritable, phExcelWritable] = {

        val reader = new PhExcelXLSXReader
        reader.setSheetindex(PhMultiSheetFactory[T].getSheetIndex)
        reader.initialize(inputSplit, taskAttemptContext)
        reader.asInstanceOf[RecordReader[NullWritable, phExcelWritable]]
    }
}

class PhXlsxFirstSheetFormat extends PhXlsxMultiSheetFormat[PhFirstSheet]
class PhXlsxSecondSheetFormat extends PhXlsxMultiSheetFormat[PhSecondSheet]
class PhXlsxThirdSheetFormat extends PhXlsxMultiSheetFormat[PhThirdSheet]
class PhXlsxFourthSheetFormat extends PhXlsxMultiSheetFormat[PhFourthSheet]
