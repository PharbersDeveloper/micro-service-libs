package com.pharbers.tools.csv

import com.pharbers.tools.base.phFileInfoTrait

/**
  * Created by clock on 18-2-27.
  */
case class phCsvFileInfo(file_local: String,
                         delimiter: String = 31.toChar.toString) extends phFileInfoTrait