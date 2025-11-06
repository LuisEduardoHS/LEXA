package com.lexa.app.data.repository

import com.lexa.app.data.models.Lawyer
import kotlinx.coroutines.flow.Flow

interface LawyerRepository {
    fun getAllLawyers(): Flow<List<Lawyer>>
}