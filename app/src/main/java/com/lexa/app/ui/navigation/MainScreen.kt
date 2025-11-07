package com.lexa.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val chatUiState by chatViewModel.uiState.collectAsState()

    // Menú Hamburguesa
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {
                DrawerContent(
                    sessions = chatUiState.allSessions,
                    onSessionClick = { sessionId ->
                        chatViewModel.loadSession(sessionId)
                        scope.launch { drawerState.close() }
                    },
                    onNewChatClick = {
                        chatViewModel.createNewSession()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        // El CONTENIDO PRINCIPAL de la app
        Scaffold(
            // Barra Inferior
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
                            icon = { Icon(painterResource(id = screen.icon), null, modifier = Modifier.size(24.dp)) },
                            label = { Text(stringResource(id = screen.title)) }
                        )
                    }
                }
            },

            // BARRA SUPERIOR
            topBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                if (currentRoute == AppScreen.Chat.route) {
                    TopAppBar(
                        title = { Text("Asistente Legal Lexa") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Abrir menú de chats"
                                )
                            }
                        }
                    )
                }
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