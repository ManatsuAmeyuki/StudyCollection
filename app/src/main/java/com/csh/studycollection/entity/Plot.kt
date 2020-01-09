package com.csh.studycollection.entity

import cn.bmob.v3.BmobObject


class Plot: BmobObject() {

    var title: String? = null

    var content: String? = null

    var pre: String? = null
    var next: String? = null

    var cardNeed: Int = 1
}