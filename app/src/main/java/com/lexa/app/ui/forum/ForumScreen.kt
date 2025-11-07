package com.lexa.app.ui.forum

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class PostMock(
    val id: String,
    val author: String,
    val content: String,
    val imageUrl: String?,
    val commentCount: Int
)

val mockPosts = listOf(
    PostMock(
        id = "1",
        author = "Abogado_Laboral_MX",
        content = "¡Claro que no es legal! La Ley Federal del Trabajo, en su Artículo 67, establece que las horas extra deben pagarse al 100% más.",
        imageUrl = "https://images.unsplash.com/photo-1759339433160-7a5828396250?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1170",
        commentCount = 12

    ),
    PostMock(
        id = "2",
        author = "Inquilino_Dudoso",
        content = "Mi casero no me quiere regresar mi depósito, dice que es para 'pintura general' aunque entregué el departamento limpio. ¿Qué puedo hacer?",
        imageUrl = null,
        commentCount = 5
    ),
    PostMock(
        id = "3",
        author = "Usuario_Preocupado",
        content = "¿Es legal que me hagan trabajar horas extra sin pagarme? Llevo 3 semanas así y mi jefe dice que es 'por la camiseta'.",
        imageUrl = null,
        commentCount = 3
    )
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen() {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nuevo post"
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 8.dp)
        ) {

            items(mockPosts) { post ->
                ForumPostCard(
                    author = post.author,
                    content = post.content,
                    imageUrl = post.imageUrl,
                    commentCount = post.commentCount,
                    onCardClick = {

                    }
                )
            }
        }
    }
}