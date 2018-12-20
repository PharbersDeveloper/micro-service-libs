package com.pharbers.pactions.excel.input.multisheet

import scala.reflect.ClassTag

object PhMultiSheetFactory {
    def apply[T <: PhMultiSheet : ClassTag](implicit tag: ClassTag[T]) : PhMultiSheet = {
        tag.runtimeClass.newInstance().asInstanceOf[T]
    }
}

trait PhMultiSheet extends ClassTag[PhMultiSheet] {
    def getSheetIndex : Int
}

class PhFirstSheet extends PhMultiSheet {
    override def getSheetIndex: Int = 0
    override def runtimeClass: Class[_] = classOf[PhFirstSheet]
}

class PhSecondSheet extends PhMultiSheet {
    override def getSheetIndex: Int = 1
    override def runtimeClass: Class[_] = classOf[PhSecondSheet]
}

class PhThirdSheet extends PhMultiSheet {
    override def getSheetIndex: Int = 2
    override def runtimeClass: Class[_] = classOf[PhThirdSheet]
}

class PhFourthSheet extends PhMultiSheet {
    override def getSheetIndex: Int = 3
    override def runtimeClass: Class[_] = classOf[PhFourthSheet]
}
