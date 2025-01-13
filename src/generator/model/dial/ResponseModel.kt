package com.epam.oko.imageDescription.data.model.dial

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    val id: String,
    val choices: List<Choise>,
    val created: Long,
    val model: String,
    @SerializedName("finish_reason")
    val anObject: String,
    val usage: Usage,
)