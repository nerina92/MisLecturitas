package edu.mis.lecturitas.ui.main

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import edu.mis.lecturitas.R
import edu.mis.lecturitas.model.Libro
import edu.mis.lecturitas.ui.bookList.BookListViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter


@Composable
fun BookListComposable(viewModel: BookListViewModel, context: Context) {
    val listaLibros by viewModel.listaLibros.observeAsState(initial = emptyList())
    val showProgressBar by viewModel.showProgressBar.observeAsState(initial = false)
    val image = painterResource(R.drawable.mis_lecturitas_download)
    Column {
        if (showProgressBar) {
            //mostrar un progres bar
            CircularProgressIndicator()
        } else {
            if (listaLibros.isNullOrEmpty()) {

            }else{
                LazyColumn {
                    listaLibros?.let {list->
                        items(list) { libro ->
                            ItemLibro(libro)
                        }
                    }

                }
            }
        }
    }
}



@Composable
fun ItemLibro(libro: Libro) {
    
    val painter: Painter = rememberImagePainter(
        data = libro.imagen,
        builder = {
            // Puedes configurar el tamaño de la imagen aquí, si es necesario
            size(300, 300)
        }
    )
    Box(
        modifier = Modifier.fillMaxSize().clickable {  },
        contentAlignment = Alignment.Center
    ) {
        Column {
            Image(
                painter = painter,
                contentDescription = null, // Añade una descripción si es necesario para accesibilidad
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(200.dp)
                    .clip(shape = CircleShape) // Clip para darle forma circular
            )
            Text(text = libro.nombre)
            Text(text = libro.autor)
        }
    }
}


