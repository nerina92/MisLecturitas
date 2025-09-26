package edu.mis.lecturitas.ui.playread

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer

class PlayReadActivity : ComponentActivity() {

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
    }
    private val viewModel: PlayReadViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //obtener url
        val urlRecibida: String? = intent.getStringExtra("EXTRA_URL")
        Log.d("PlayReadActivity", "URL Recibida: $urlRecibida")

        setContent {
            MisLecturitasTheme {
                if (urlRecibida != null) {
                    PlayReadScreen(url = urlRecibida)
                } else {
                    // Manejar el caso de URL nula si es necesario
                    Text("Error: URL no encontrada")
                }
            }
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
fun PlayReadScreen(url: String) {
    var showWebView by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (showWebView) {
            WebViewComponent(modifier = Modifier.padding(innerPadding), url = url)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { /* TODO: Acción Jugar */ }) {
                    Text("Jugar")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showWebView = true }) {
                    Text("Leer")
                }
            }
        }
    }
}
