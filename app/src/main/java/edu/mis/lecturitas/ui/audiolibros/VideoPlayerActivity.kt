package edu.mis.lecturitas.ui.audiolibros

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

class VideoPlayerActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Mantener la pantalla encendida durante la reproducción
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
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
        // Limpiar la bandera de mantener pantalla encendida
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
            
            // Información adicional
            /*Text(
                text = "Por: ${audioLibro.autor} • Duración: ${audioLibro.duracion}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            if (audioLibro.descripcion.isNotEmpty()) {
                Text(
                    text = audioLibro.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Botón para alternar modo pantalla completa
            Button(
                onClick = { isFullscreen = !isFullscreen },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(if (isFullscreen) "📱 Salir de pantalla completa" else "🖥️ Pantalla completa")
            }*/
            
            // Reproductor de video con WebView
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
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
                            
                            /*webChromeClient = object : WebChromeClient() {
                                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                    Log.i("VideoPlayer", "Video en pantalla completa")
                                    isFullscreen = true
                                }
                                
                                override fun onHideCustomView() {
                                    Log.i("VideoPlayer", "Salir de pantalla completa")
                                    isFullscreen = false
                                }
                            }*/
                            
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    Log.i("VideoPlayer", "Página cargada: $url")
                                }
                            }
                            
                            // Crear HTML con reproductor de video
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
                    modifier = //if (isFullscreen) {
                        Modifier.fillMaxSize()
                   /* } else {
                        Modifier
                            .fillMaxSize()
                            .aspectRatio(16f / 9f) // Mantener proporción 16:9 solo en modo normal
                    }*/
                )
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
