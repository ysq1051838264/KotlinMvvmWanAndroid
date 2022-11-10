package com.kotlin.mvvm.ui.integral.bean

/**
 * description:
 *
 * @author Db_z
 * @Date 2022/1/22 15:56
 */
data class UserIntegralBean(
    val coinCount: Int,
    val level: Int,
    val nickname: String,
    val rank: String,
    val userId: Int,
    val username: String
)