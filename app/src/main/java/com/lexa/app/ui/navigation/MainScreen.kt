package com.lexa.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lexa.app.ui.chat.ChatScreen
import com.lexa.app.ui.chat.ChatViewModel
import com.lexa.app.ui.forum.ForumScreen
import com.lexa.app.ui.lawyers.LawyersScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val chatViewModel: ChatViewModel = hiltViewModel()

    // Controladores para el Menú y la Navegación
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // CONTENIDO del menú
            ModalDrawerSheet {

                Text("Contenido del Menú Hamburguesa", modifier = Modifier.padding(16.dp))
            }
        }
    ) {
        // CONTENIDO PRINCIPALe)
        Scaffold(
            // ARRA INFERIOR
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text(stringResource(id = screen.title)) }
                        )
                    }
                }
            },
            // BARRA SUPERIOR
            topBar = {

            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppScreen.Chat.route,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(AppScreen.Chat.route) {
                    ChatScreen(viewModel = chatViewModel)
                }
                composable(AppScreen.Lawyers.route) { LawyersScreen() }
                composable(AppScreen.Forum.route) { ForumScreen() }
            }
        }
    }
}