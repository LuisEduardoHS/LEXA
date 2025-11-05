@file:OptIn(
    kotlinx.serialization.ExperimentalSerializationApi::class,
    kotlinx.serialization.InternalSerializationApi::class
)

package com.lexa.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class HuggingFaceResponse(
    val generated_text: String
)
