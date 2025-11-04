package com.lexa.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lexa.app.ui.navigation.MainScreen // 1. Importa nuestra pantalla principal
import com.lexa.app.ui.theme.LEXATheme
import dagger.hilt.android.AndroidEntryPoint

// 2. ¡MUY IMPORTANTE! Esta anotación le dice a Hilt
// que esta Activity necesitará inyecciones (como ViewModels más adelante).
// Si la olvidas, la app crasheará.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Permite que la app se dibuje "de borde a borde" (pantalla completa moderna)
        enableEdgeToEdge()

        setContent {
            // 4. Aplicamos nuestro tema (colores, fuentes)
            LEXATheme {
                // 5. ¡Mostramos nuestra MainScreen!
                // Esta pantalla ahora contiene toda la lógica de navegación.
                MainScreen()
            }
        }
    }
}