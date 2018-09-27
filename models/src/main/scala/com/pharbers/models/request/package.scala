package com.pharbers.models

package object request {

    def eq2c(key: String, `val`: Any, category: Any = null): eqcond = {
        val tmp = new eqcond
        tmp.key = key
        tmp.`val` = `val`
        tmp.category = category
        tmp
    }

    def ne2c(key: String, `val`: Any, category: Any = null): necond = {
        val tmp = new necond
        tmp.key = key
        tmp.`val` = `val`
        tmp.category = category
        tmp
    }

    def gt2c(key: String, `val`: Any, category: Any = null): gtcond = {
        val tmp = new gtcond
        tmp.key = key
        tmp.`val` = `val`
        tmp.category = category
        tmp
    }

    def gte2c(key: String, `val`: Any, category: Any = null): gtecond = {
        val tmp = new gtecond
        tmp.key = key
        tmp.`val` = `val`
        tmp.category = category
        tmp
    }

    def lt2c(key: String, `val`: Any, category: Any = null): ltcond = {
        val tmp = new ltcond
        tmp.key = key
        tmp.`val` = `val`
        tmp.category = category
        tmp
    }

    def lte2c(key: String, `val`: Any, category: Any = null): ltecond = {
        val tmp = new ltecond
        tmp.key = key
        tmp.`val` = `val`
        tmp.category = category
        tmp
    }

    def fm2c(skip: Int = 0, take: Int = 20): fmcond = {
        val tmp = new fmcond
        tmp.skip = skip
        tmp.take = take
        tmp
    }

    def up2c(key: String, `val`: Any): upcond = {
        val tmp = new upcond
        tmp.key = key
        tmp.`val` = `val`
        tmp
    }

}
