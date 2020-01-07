package com.csh.studycollection.entity

import cn.bmob.v3.BmobUser

/**
 * created by shenghuiche on 2019-12-30
 */
class User: BmobUser() {

    // 积分
    var integral: Int? = null

    var grade: Grade? = null

    var area: Area? = null

}