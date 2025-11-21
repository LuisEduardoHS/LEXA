package com.lexa.app.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.lexa.app.R

// Contendra la definicion de cada una de nuestras pantallas
sealed class AppScreen (
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
){
    // Definimos la pantalla de Chat
    data object Chat : AppScreen(
        route = "chat",
        title = R.string.screen_title_chat,
        icon = R.drawable.ic_chat
    )

    // Definimos la pantalla de Abogados
    data object Lawyers : AppScreen(
        route = "lawyers",
        title = R.string.screen_title_lawyers,
        icon = R.drawable.ic_lawyers
    )

    // Definimos la pantalla de Foro
    data object Forum : AppScreen(
        route = "forum",
        title = R.string.screen_title_forum,
        icon = R.drawable.ic_forum
    )

    data object NewPost : AppScreen(
        route = "new_post",
        title = R.string.screen_title_forum,
        icon = R.drawable.ic_forum
    )
}

val bottomNavScreens = listOf(
    AppScreen.Chat,
    AppScreen.Lawyers,
    AppScreen.Forum,
)