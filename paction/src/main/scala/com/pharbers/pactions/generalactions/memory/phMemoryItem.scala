package com.pharbers.pactions.generalactions.memory

import org.apache.spark.sql.DataFrame

case class phMemoryItem(company: String){

    val name: String = company + "_DataFrame_Manage"
    var memory: Map[String, DataFrame] = Map.empty

    def registerMemory(k: String, df: DataFrame): phMemoryItem = {
        df.persist()
        memory += (k -> df)
        this
    }

    def queryMemory(k: String): DataFrame = memory.get(k) match {
        case Some(df) => df
        case None => throw new Exception(s"not found key=$k in PhMemory.memory.$company")
    }

    def clean(k : String): DataFrame = {
        memory -= k
        queryMemory(k).unpersist()
    }

    def clean(): Unit = {
        memory foreach { x =>
            x._2.unpersist()
            memory -= x._1
        }
    }

    def isExist(k : String): Boolean = {
        memory.contains(k)
    }

}
