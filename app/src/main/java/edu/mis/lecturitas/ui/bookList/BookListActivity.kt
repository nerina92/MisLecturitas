package edu.mis.lecturitas.ui.bookList

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import edu.mis.lecturitas.R
import edu.mis.lecturitas.model.Libro
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.bookList.ui.theme.MisLecturitasTheme
import edu.mis.lecturitas.ui.juegos.RompecabezasActivity
import edu.mis.lecturitas.ui.main.MainActivity
import edu.mis.lecturitas.ui.main.MainViewModel
import edu.mis.lecturitas.ui.playread.PlayReadActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookListActivity : ComponentActivity() {
    private val viewModel: BookListViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nivel = intent.getIntExtra("nivel",0)
        viewModel.consultarLibnos(nivel)
        setContent {
            MisLecturitasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookListComposable(viewModel)
                }
            }
        }
        viewModel.openCuento.observe(this){
            if (it!=null){
                openCuento(it)
                //openPlayRead(it)
                //openWebViewCuento(it)
                viewModel.setOpenCuentoNull()
            }

        }
        
        viewModel.openPlayRead.observe(this){
            if (it!=null){
                openRompecabezas(it.first, it.second)
                viewModel.setOpenPlayReadNull()
            }
        }
        
        viewModel.showBookOptions.observe(this){
            // El Bottom Sheet se manejará en el composable
        }
        viewModel.goBack.observe(this){
            if(it){
                onBackPressed()
                viewModel.doneGoBack()
            }
        }
    }
    fun openCuento(url: String){
        // Crear una intención para abrir el archivo PDF con la aplicación adecuada
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "application/pdf")
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openPlayRead(url: String?, imagen: String?) {
        // Crear una intención para abrir la PlayReadActivity
        val intent = Intent(this, PlayReadActivity::class.java).apply {
            // Añadir la URL y la imagen como extras al Intent
            putExtra("EXTRA_URL", url)
            putExtra("EXTRA_IMAGE", imagen)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Considera mostrar un mensaje al usuario si ocurre un error
        }
    }
    fun openRompecabezas(url: String?, imagen: String?) {
        // Crear una intención para abrir el juego de rompecabezas
        val intent = Intent(this, RompecabezasActivity::class.java).apply {
            putExtra("EXTRA_URL", url)
            putExtra("EXTRA_IMAGE", imagen)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}

// Dialog Modal para las opciones del libro
@Composable
fun BookOptionsDialog(
    libro: Libro?,
    onDismiss: () -> Unit,
    onJugar: (String?, String?) -> Unit,
    onLeer: (String) -> Unit
) {
    libro?.let { book ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "¿Qué quieres hacer?",
                    fontFamily = FontFamily(Font(R.font.league_spartan_medium)),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Imagen del libro
                    val painter: Painter = rememberImagePainter(
                        data = book.imagen,
                        builder = {
                            size(100, 100)
                        }
                    )
                    
                    Image(
                        painter = painter,
                        contentDescription = "Imagen del libro",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Text(
                        text = book.nombre.uppercase(),
                        fontFamily = FontFamily(Font(R.font.league_spartan_medium)),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = book.autor,
                        fontFamily = FontFamily(Font(R.font.league_spartan_light)),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onJugar(book.url, book.imagen)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "🎮 JUGAR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onLeer(book.url)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "📖 LEER",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        )
    }
}

@Composable
fun BookListComposable(viewModel: BookListViewModel) {
    val listaLibros: ArrayList<Libro>? by viewModel.listaLibros.observeAsState(initial = null)
    val showProgressBar: Boolean by viewModel.showProgressBar.observeAsState(initial = false)
    val selectedBook: Libro? by viewModel.showBookOptions.observeAsState(initial = null)
    val image = painterResource(R.drawable.mis_lecturitas_download)
    
    Column {
    MyToolbar({ viewModel.backPresed() })
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {

        if (showProgressBar) {
            //mostrar un progres bar
            CircularProgressIndicator()
        } else {
            if (listaLibros.isNullOrEmpty()) {
                Text(text = "No hay libros para mostrar")
            } else {
                LazyColumn {
                    listaLibros?.let { list: ArrayList<Libro> ->
                        items(list) { libro ->
                            BookItem(libro, { viewModel.showBookOptions(libro) })
                        }
                    }

                }
            }
        }
    }
    
    // Dialog para las opciones del libro
    BookOptionsDialog(
        libro = selectedBook,
        onDismiss = { viewModel.hideBookOptions() },
        onJugar = { url, imagen -> 
            viewModel.openPlayRead(url, imagen)
        },
        onLeer = { url -> 
            viewModel.openCuento.postValue(url)
        }
    )
    }
}



@Composable
fun BookItem(libro: Libro, onClick: () -> Unit) {
    Log.d("Item libro","libro: $libro")
    val painter: Painter = rememberImagePainter(
        data = libro.imagen,
        builder = {
            // Puedes configurar el tamaño de la imagen aquí, si es necesario
            size(70, 70)
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = Color(0xFFF18162))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row (horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(10.dp)){
            Image(
                painter = painter,
                contentDescription = null, // Añade una descripción si es necesario para accesibilidad
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(shape = CircleShape) // Clip para darle forma circular
            )
            Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                Text(
                    text = libro.nombre.uppercase(),
                    fontFamily = FontFamily(Font(R.font.league_spartan_medium)),
                    fontSize = 20.sp
                    )
                Text(
                    text = libro.autor,
                    fontFamily = FontFamily(Font(R.font.league_spartan_light)),
                    fontSize = 18.sp
                )
            }
        }

    }
}




@Preview(showBackground = true)
@Composable
fun BookItemPreview2() {
    MisLecturitasTheme {
        //BookItem(Libro(1, "Libro 1", "Autor 1", "", "", null, null, "", 0),{})
        BookListComposable(BookListViewModel())
    }
}
