package com.pharbers.dbManagerTrait

import com.pharbers.mongodbConnect.connection_instance

class dbInstance(cp : String) extends connection_instance {
    override val configPath: String = cp
}
