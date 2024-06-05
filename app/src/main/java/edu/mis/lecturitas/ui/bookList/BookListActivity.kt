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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import edu.mis.lecturitas.R
import edu.mis.lecturitas.model.Libro
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.bookList.ui.theme.MisLecturitasTheme
import edu.mis.lecturitas.ui.main.MainViewModel
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
                //openWebViewCuento(it)
                viewModel.setOpenCuentoNull()
            }

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

    fun openWebViewCuento(url:String){
        val webView: WebView = findViewById(R.id.my_webview)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // Puedes realizar alguna acción cuando la página comienza a cargarse
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Puedes realizar alguna acción cuando la página ha terminado de cargarse
            }
        }
        // Cargar la URL directa del PDF en el WebView
        webView.loadUrl(url)
    }
}

@Composable
fun BookListComposable(viewModel: BookListViewModel) {
    val listaLibros by viewModel.listaLibros.observeAsState(initial = emptyList())
    val showProgressBar by viewModel.showProgressBar.observeAsState(initial = false)
    val image = painterResource(R.drawable.mis_lecturitas_download)
Column{
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
                    listaLibros?.let { list ->
                        items(list) { libro ->
                            BookItem(libro, { viewModel.openCuento.postValue(libro.url) })
                        }
                    }

                }
            }
        }
    }
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
