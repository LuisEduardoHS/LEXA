package com.lexa.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar // Importante: No es BottomNavigation
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lexa.app.ui.chat.ChatScreen
import com.lexa.app.ui.forum.ForumScreen
import com.lexa.app.ui.lawyers.LawyersScreen

@Composable
fun MainScreen(){
    // 1. Crea el controlador de navegacion, que recuerda donde estamos
    val navController = rememberNavController()

    // 2. Scaffold es la estructura base de Material 3 (con barra superior, inferior, etc.)
    Scaffold(
        bottomBar = {
            // 3. Esta es la barra de navegacion inferior
            NavigationBar {
                // 4. Observamos la pila de navegacion para saber que pantalla esta activa
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // 5. Recorremos la lista de pantallas (Chat, Lawyers, Forum)
                bottomNavScreens.forEach { screen ->
                    // 6. Creamos un item en la barra por cada pantalla
                    NavigationBarItem(
                        // Comprueba si la ruta de la pantalla actual esta en la jerarquia de navegacion
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            // 7. logica para navegar al hacer clic
                            navController.navigate(screen.route){
                                // Evita acumular historial de navegacion si se presiona el mismo item
                                popUpTo(navController.graph.findStartDestination().id){
                                    saveState = true
                                }
                                // Lanza la pantalla como "unica" en la pila
                                launchSingleTop = true
                                // Restaura el estado si se vuelve a una pantalla anterior
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = stringResource(id = screen.title),
                                modifier = Modifier.size(24.dp)

                            )
                        },
                        label = {Text(stringResource(id = screen.title))}
                    )
                }
            }
        }
    ){ innerPadding -> // 8. Este es el contenido principal de la pantalla

        // 9. NavHost es el "contenedor" que intercambia las pantallas
        NavHost(
            navController = navController, // Empezamos con el chat
            startDestination = AppScreen.Chat.route, // Aplicamos el padding del Scaffold
            modifier = Modifier.padding(innerPadding)
        ){
            // 10. Define cada "ruta" y que Composable mostrar
            composable(AppScreen.Chat.route) { ChatScreen() }
            composable(AppScreen.Lawyers.route) { LawyersScreen() }
            composable(AppScreen.Forum.route) { ForumScreen() }
        }

    }
}