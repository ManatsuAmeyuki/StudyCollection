package com.csh.studycollection.entity

import cn.bmob.v3.BmobObject

class Coupon(var userId: String? = null, var card: Card? = null, var status: Int = 0): BmobObject() {
}