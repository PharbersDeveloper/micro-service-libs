package com.pharbers.pactions.generalactions

import scala.reflect.ClassTag
import org.apache.hadoop.io.NullWritable
import com.pharbers.pactions.actionbase._
import com.pharbers.excel.format.input.writable.phExcelWritable
import com.pharbers.spark.phSparkDriver
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat

object xlsReadingAction {
    def apply[T <: FileInputFormat[NullWritable, phExcelWritable] : ClassTag](arg_path: String, arg_name: String, applicationName: String): pActionTrait =
        new xlsReadingAction[T](StringArgs(arg_path), arg_name, applicationName)
}

class xlsReadingAction[T <: FileInputFormat[NullWritable, phExcelWritable] : ClassTag](override val defaultArgs: pActionArgs,
                                                                                       override val name: String,
                                                                                       applicationName: String) extends pActionTrait {
    override def perform(args: pActionArgs): pActionArgs = {
        RDDArgs(phSparkDriver(applicationName).sc.
                newAPIHadoopFile[NullWritable, phExcelWritable, T](defaultArgs.get.toString).map(x => x._2))
    }
}
