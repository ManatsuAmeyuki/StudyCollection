package com.csh.studycollection.entity

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobFile

class Exercise(
    var user: User? = null,
    var topic: Topic? = null,
    var answer: String? = null,
    var answerFileUrl: String? = null,
    var localUrl: String? = null,
    var status: Int = 0
) : BmobObject() {

    override fun toString(): String {
        return "${answer}/${answerFileUrl}"
    }

}