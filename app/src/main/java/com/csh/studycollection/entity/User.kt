package com.csh.studycollection.entity

import cn.bmob.v3.BmobUser


class User: BmobUser() {

    // 积分
    var integral: Int? = null

    var grade: Grade? = null

    var area: Area? = null

}