package com.pharbers.pactions.generalactions

import scala.reflect.ClassTag
import com.pharbers.spark.phSparkDriver
import org.apache.hadoop.io.NullWritable
import com.pharbers.pactions.actionbase._
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import com.pharbers.excel.format.input.writable.phExcelWritable

object xlsReadingAction {
    def apply[T <: FileInputFormat[NullWritable, phExcelWritable]
    : ClassTag](arg_path: String, arg_name: String)
               (implicit sparkDriver: phSparkDriver): pActionTrait =
        new xlsReadingAction[T](StringArgs(arg_path), arg_name)
}

class xlsReadingAction[T <: FileInputFormat[NullWritable, phExcelWritable]
: ClassTag](override val defaultArgs: pActionArgs, override val name: String)
           (implicit sparkDriver: phSparkDriver) extends pActionTrait {
    override def perform(args: pActionArgs = NULLArgs): pActionArgs = {
        RDDArgs(
            sparkDriver.sc.newAPIHadoopFile[NullWritable, phExcelWritable, T]
                    (defaultArgs.get.toString)
                    .map(x => x._2)
        )
    }
}
