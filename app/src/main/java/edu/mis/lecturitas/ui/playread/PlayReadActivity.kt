package edu.mis.lecturitas.ui.playread

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.playread.ui.theme.MisLecturitasTheme
import edu.mis.lecturitas.ui.juegos.RompecabezasActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import edu.mis.lecturitas.repository.GamificationRepository
import edu.mis.lecturitas.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayReadActivity : ComponentActivity() {

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_IMAGE = "EXTRA_IMAGE"
    }
    private val viewModel: PlayReadViewModel by viewModel()
    private val gamificationRepository = GamificationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //obtener url e imagen
        val urlRecibida: String? = intent.getStringExtra("EXTRA_URL")
        val imagenRecibida: String? = intent.getStringExtra("EXTRA_IMAGE")
        Log.d("PlayReadActivity", "URL Recibida: $urlRecibida")
        Log.d("PlayReadActivity", "Imagen Recibida: $imagenRecibida")

        setContent {
            MisLecturitasTheme {
                if (urlRecibida != null) {
                    PlayReadScreen(
                        url = urlRecibida,
                        imagen = imagenRecibida,
                        onOpenCuento = { url -> openCuento(url) },
                        onJugar = { url, imagen -> openRompecabezas(url, imagen) }
                    )
                } else {
                    // Manejar el caso de URL nula si es necesario
                    Text("Error: URL no encontrada")
                }
            }
        }
    }
    fun openCuento(url: String){
        // Registrar que el usuario leyó un libro
        recordBookActivity()

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

    private fun recordBookActivity() {
        val currentUser = UserRepository.getCurrentUser()
        if (currentUser != null && !currentUser.user.equals("invitado", ignoreCase = true)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    gamificationRepository.recordActivity(
                        userId = currentUser.user,
                        activityType = "BOOK"
                    )
                    Log.d("Gamification", "Libro registrado para usuario: ${currentUser.user}")
                } catch (e: Exception) {
                    Log.e("Gamification", "Error al registrar libro", e)
                }
            }
        } else {
            Log.d("Gamification", "Usuario invitado - no se registra actividad")
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MisLecturitasTheme {
        Greeting("Android")
    }
}
@Composable
fun PlayReadScreen(
    url: String,
    imagen: String?,
    onOpenCuento: (String) -> Unit,
    onJugar: (String?, String?) -> Unit
) {
    var showWebView by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (showWebView) {
            Scaffold(modifier = Modifier.fillMaxSize()) { webViewInnerPadding ->
                // WebViewComponent(modifier = Modifier.padding(webViewInnerPadding), url = url)
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            loadUrl(url)
                        }
                    }, modifier = Modifier.padding(webViewInnerPadding))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { onJugar(url, imagen) }) {
                    Text("Jugar")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onOpenCuento(url) }) {
                    Text("Leer")
                }
            }
        }
    }
}
@Composable
fun WebViewComponent(modifier: Modifier = Modifier, url: String) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}
