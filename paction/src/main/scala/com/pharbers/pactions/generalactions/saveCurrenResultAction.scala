package com.pharbers.pactions.generalactions

import scala.reflect.ClassTag
import org.apache.hadoop.io.compress.GzipCodec
import com.pharbers.pactions.actionbase.{RDDArgs, StringArgs, pActionArgs, pActionTrait}

object saveCurrenResultAction {
    def apply[T: ClassTag](arg_path: String,
                           arg_name: String = "saveCurrenResultJob"): pActionTrait =
        new saveCurrenResultAction[T](StringArgs(arg_path), arg_name)
}

class saveCurrenResultAction[T: ClassTag](override val defaultArgs: pActionArgs,
                                          override val name: String) extends pActionTrait {

    override def perform(args: pActionArgs): pActionArgs = {
        val rdd = args.asInstanceOf[RDDArgs[T]].get
        rdd.saveAsTextFile(defaultArgs.asInstanceOf[StringArgs].get, classOf[GzipCodec])
        args
    }
}
