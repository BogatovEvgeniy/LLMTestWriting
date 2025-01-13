package com.epam.oko.imageDescription.data.model.dial

import com.google.gson.annotations.SerializedName

data class Choise(
    @SerializedName("finish_reason")
    val finishReason: String,
    val index: Int,
    val message: Message,
)