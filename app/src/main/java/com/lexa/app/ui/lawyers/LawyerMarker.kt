package com.lexa.app.ui.lawyers

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.lexa.app.R

@Composable
fun LawyerMarker(
    imageUrl: String?
) {
    val context = LocalContext.current

    // 1. Estado para guardar la imagen descargada (Bitmap)
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 2. Efecto: Se ejecuta cuando cambia la URL
    LaunchedEffect(imageUrl) {
        if (imageUrl != null) {
            // Cargador de Coil
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false) // ¡CRUCIAL PARA MAPAS!
                .build()

            // Descargamos la imagen en segundo plano
            val result = loader.execute(request)

            // Si se descargó bien, la guardamos en el estado
            if (result is SuccessResult) {
                imageBitmap = (result.drawable as BitmapDrawable).bitmap
            }
        }
    }

    // 3. Diseño del Pin
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // 4. Lógica de visualización
        if (imageBitmap != null) {
            // SI YA DESCARGÓ: Mostramos la foto real
            Image(
                bitmap = imageBitmap!!.asImageBitmap(), // Convertimos a formato Compose
                contentDescription = "Foto abogado",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
        } else {
            // SI NO HA DESCARGADO (o falló): Mostramos el icono rojo
            Image(
                painter = painterResource(id = R.drawable.ic_lawyer_pin_red),
                contentDescription = "Loading",
                modifier = Modifier
                    .size(32.dp) // Un poco más chico para que se vea bien dentro
            )
        }
    }
}