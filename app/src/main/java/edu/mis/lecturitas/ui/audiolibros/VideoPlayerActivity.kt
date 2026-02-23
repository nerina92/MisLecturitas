package edu.mis.lecturitas.ui.audiolibros

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import edu.mis.lecturitas.model.AudioLibro
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import edu.mis.lecturitas.repository.GamificationRepository
import edu.mis.lecturitas.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoPlayerActivity : ComponentActivity() {

    private val gamificationRepository = GamificationRepository()
    private var videoStartTime: Long = 0
    private val MIN_VIEW_TIME_SECONDS = 30 // Mínimo 30 segundos para contar como "visto"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantener la pantalla encendida durante la reproducción
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Registrar tiempo de inicio
        videoStartTime = System.currentTimeMillis()

        val audioLibro = intent.getSerializableExtra("audioLibro") as? AudioLibro

        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoPlayerComposable(
                        audioLibro = audioLibro,
                        onBackPressed = { onBackPressed() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Registrar actividad si vio al menos MIN_VIEW_TIME_SECONDS segundos
        val viewTimeSeconds = (System.currentTimeMillis() - videoStartTime) / 1000
        if (viewTimeSeconds >= MIN_VIEW_TIME_SECONDS) {
            recordAudiobookActivity()
        } else {
            Log.d("Gamification", "Audiolibro no registrado - tiempo de visualización insuficiente: ${viewTimeSeconds}s")
        }

        // Limpiar la bandera de mantener pantalla encendida
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun recordAudiobookActivity() {
        val currentUser = UserRepository.getCurrentUser()
        if (currentUser != null && !currentUser.user.equals("invitado", ignoreCase = true)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    gamificationRepository.recordActivity(
                        userId = currentUser.user,
                        activityType = "AUDIOBOOK"
                    )
                    Log.d("Gamification", "Audiolibro registrado para usuario: ${currentUser.user}")
                } catch (e: Exception) {
                    Log.e("Gamification", "Error al registrar audiolibro", e)
                }
            }
        } else {
            Log.d("Gamification", "Usuario invitado - no se registra actividad")
        }
    }
}

@Composable
fun VideoPlayerComposable(
    audioLibro: AudioLibro?,
    onBackPressed: () -> Unit
) {
    //var isFullscreen by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column {
        MyToolbar({ onBackPressed() })

        if (audioLibro != null) {
            // Título del audiolibro
            Text(
                text = audioLibro.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            // Reproductor de video con WebView
            val pm = context.packageManager
            val webViewAvailable = try {
                pm.getPackageInfo("com.google.android.webview", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                if (webViewAvailable) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    mediaPlaybackRequiresUserGesture = false
                                    domStorageEnabled = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        Log.i("VideoPlayer", "Página cargada: $url")
                                    }
                                }
                                val html = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <style>
                                body { margin: 0; padding: 0; background: black; }
                                video { width: 100%; height: 100%; object-fit: contain; }
                            </style>
                        </head>
                        <body>
                            <video controls autoplay>
                                <source src="${audioLibro.urlVideo}" type="video/mp4">
                                Tu navegador no soporta el elemento video.
                            </video>
                        </body>
                        </html>
                    """.trimIndent()
                                loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "No se puede mostrar el video. Verifica que WebView esté instalado y habilitado.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }


        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: No se pudo cargar el audiolibro",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
}

// Limpiar el reproductor cuando se destruye el composable
}
