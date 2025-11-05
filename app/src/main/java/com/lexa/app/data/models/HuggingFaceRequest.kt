@file:OptIn(
    kotlinx.serialization.ExperimentalSerializationApi::class,
    kotlinx.serialization.InternalSerializationApi::class
)

package com.lexa.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class HuggingFaceRequest(
    val inputs: String
)
