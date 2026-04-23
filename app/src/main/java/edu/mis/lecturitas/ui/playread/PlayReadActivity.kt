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
import edu.mis.lecturitas.utils.ActivityType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

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

        Log.d("PlayReadActivity", "Intentando abrir PDF con URL: $url")

        // Validar que la URL no esté vacía
        if (url.isBlank()) {
            android.widget.Toast.makeText(
                this,
                "Error: URL del PDF no válida",
                android.widget.Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            // Si la URL es de Firebase Storage, usar Google Docs Viewer (más confiable)
            if (url.contains("firebasestorage.googleapis.com") || url.contains("firebase")) {
                Log.d("PlayReadActivity", "URL de Firebase detectada, usando Google Docs Viewer")
                openPdfWithGoogleDocs(url)
                return
            }

            val uri = Uri.parse(url)
            Log.d("PlayReadActivity", "URI parseada: $uri, scheme: ${uri.scheme}")

            // Crear intención para abrir PDF
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Verificar si hay una app que pueda abrir PDFs
            val packageManager = packageManager
            val activities = packageManager.queryIntentActivities(intent, 0)

            if (activities.size > 0) {
                startActivity(intent)
            } else {
                // No hay app para abrir PDFs, usar Google Docs como fallback
                Log.w("PlayReadActivity", "No se encontró app para PDFs, usando Google Docs Viewer")
                openPdfWithGoogleDocs(url)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PlayReadActivity", "Error al abrir PDF: ${e.message}", e)

            // Intentar con Google Docs como último recurso
            try {
                openPdfWithGoogleDocs(url)
            } catch (e2: Exception) {
                android.widget.Toast.makeText(
                    this,
                    "Error al abrir el PDF: ${e.message}\nURL: $url",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openPdfWithGoogleDocs(pdfUrl: String) {
        // Usar Google Docs Viewer para abrir el PDF
        val googleDocsUrl = "https://docs.google.com/viewer?url=${Uri.encode(pdfUrl)}"
        Log.d("PlayReadActivity", "Abriendo con Google Docs Viewer: $googleDocsUrl")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(googleDocsUrl)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("PlayReadActivity", "Error al abrir con Google Docs", e)
            android.widget.Toast.makeText(
                this,
                "No se pudo abrir el PDF. Por favor, verifica tu conexión a internet.",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun recordBookActivity() {
        val currentUser = UserRepository.getCurrentUser()
        if (currentUser != null && !currentUser.user.equals("invitado", ignoreCase = true)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Obtener progreso actual del usuario
                    val currentProgress = gamificationRepository.getUserProgress(currentUser.user).first()

                    if (currentProgress != null) {
                        gamificationRepository.recordActivity(
                            userId = currentUser.user,
                            activityType = ActivityType.BOOK_READ,
                            currentProgress = currentProgress
                        )
                        Log.d("Gamification", "Libro registrado para usuario: ${currentUser.user}")
                    } else {
                        // Inicializar progreso si no existe
                        gamificationRepository.initializeUserProgress(currentUser.user)
                        val newProgress = gamificationRepository.getUserProgress(currentUser.user).first()
                        if (newProgress != null) {
                            gamificationRepository.recordActivity(
                                userId = currentUser.user,
                                activityType = ActivityType.BOOK_READ,
                                currentProgress = newProgress
                            )
                        }
                    }
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
