package com.pharbers.pactions.generalactions.memory

import org.apache.spark.sql.DataFrame

object phMemory {

    var memory: Map[String, phMemoryItem] = Map.empty

    def registerMemory(k: String, df: DataFrame)(implicit company: phMemoryArgs): Unit = {
        val cm = memory.get(company.get) match {
            case Some(x) => x
            case None => phMemoryItem(company.get)
        }

        println(s"registerMemory = " + cm)

        memory += (company.get -> cm.registerMemory(k, df))
    }

    def queryMemory(k: String)(implicit company: phMemoryArgs): DataFrame = {
        val a = memory.get(company.get) match {
            case Some(cm) => cm.queryMemory(k)
            case None => throw new Exception(s"not found company=${company.get} in PhMemory.memory")
        }

        println(s"queryMemory = " + a)

        a
    }

    def clean(k: String)(implicit company: phMemoryArgs): DataFrame = {
        memory.get(company.get) match {
            case Some(cm) => cm.clean(k)
            case None => throw new Exception(s"not found company=${company.get} in PhMemory.memory")
        }
    }

    def clean()(implicit company: phMemoryArgs): Unit = {
        memory.get(company.get) match {
            case Some(cm) => cm.clean
            case None => throw new Exception(s"not found company=${company.get} in PhMemory.memory")
        }
    }

    def isExist(k: String)(implicit company: phMemoryArgs) : Boolean = {
        val a = memory.get(company.get) match {
            case Some(cm) => cm.isExist(k)
            case None => throw new Exception(s"not found company=${company.get} in PhMemory.memory")
        }

        println(s"isExist = " + a)

        a
    }

}

