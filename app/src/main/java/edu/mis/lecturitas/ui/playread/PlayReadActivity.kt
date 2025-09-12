package edu.mis.lecturitas.ui.playread

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.playread.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer

class PlayReadActivity : ComponentActivity() {

    private val viewModel: PlayReadViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //obtener url
        val urlRecibida: String? = intent.getStringExtra("EXTRA_URL")

        setContent {
            MisLecturitasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlayRead(modifier = Modifier.padding(innerPadding), urlRecibida)
                }
            }
        }

        viewModel.openCuento.observe(this, Observer { value ->
            if (value == true){
                urlRecibida?.let { openWebViewCuento(it) }
                viewModel.setOpenCuentoFalse()
            }
        })
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