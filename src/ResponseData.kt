package cn.luedian.t

import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(val success: Boolean, val value: String)