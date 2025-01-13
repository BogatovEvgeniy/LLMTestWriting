package com.epam.oko.imageDescription.data.model.dial

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val messages: List<Message>,
    val temperature: Int = 1,
    val model: String,
    val store: Boolean,
    @SerializedName("max_tokens")
    val maxTokens: Int = 1000,
)