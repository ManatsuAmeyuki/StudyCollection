package com.csh.studycollection.entity

import cn.bmob.v3.BmobObject


class Topic: BmobObject() {
    var question: String? = null
    var solution: String? = null
    var options: String? = null
    var subjectCode: String? = null
    var type: Int? = null
    var level: Int? = null

    var gradeCode: String? = null
    var areaCode: String? = null

}