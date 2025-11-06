package com.lexa.app.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Lawyer(
    @DocumentId
    val id: String = "",

    val name: String = "",
    val specialty: String = "",
    val location: GeoPoint? = null
)
