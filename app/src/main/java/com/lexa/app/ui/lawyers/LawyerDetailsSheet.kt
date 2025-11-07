package com.lexa.app.ui.lawyers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexa.app.data.models.Lawyer

@Composable
fun LawyerDetailSheet(lawyer: Lawyer) {
    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = lawyer.name,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = lawyer.specialty,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Informacion de contacto, biografia y boton de 'contactar' irian aqui.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}