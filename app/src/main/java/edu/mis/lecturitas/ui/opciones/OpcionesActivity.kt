package edu.mis.lecturitas.ui.opciones

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.audiolibros.AudioLibrosActivity
import edu.mis.lecturitas.ui.juegos.JuegosActivity
import edu.mis.lecturitas.ui.main.MainActivity
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpcionesActivity : ComponentActivity() {
    private val viewModel: OpcionesViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OpcionesActivityComposable(viewModel)
                }
            }
        }
        
        viewModel.goBack.observe(this) {
            if (it) {
                onBackPressed()
                viewModel.doneGoback()
            }
        }
        
        viewModel.openJuegos.observe(this) {
            if (it) {
                openJuegosActivity()
                viewModel.setOpenJuegosFalse()
            }
        }
        
        viewModel.openAudiolibros.observe(this) {
            if (it) {
                openAudiolibrosActivity()
                viewModel.setOpenAudiolibrosFalse()
            }
        }
    }
    
    private fun openJuegosActivity() {
        val intent = Intent(this, JuegosActivity::class.java)
        startActivity(intent)
    }
    
    private fun openAudiolibrosActivity() {
        val intent = Intent(this, AudioLibrosActivity::class.java)
        // Pasar -1 para mostrar todos los audiolibros, o un nivel específico si se desea filtrar
        intent.putExtra("nivel", -1)
        startActivity(intent)
    }
}

@Composable
fun OpcionesActivityComposable(viewModel: OpcionesViewModel) {
    val image = painterResource(R.drawable.dibujo5)
    
    Column {
        MyToolbar({ viewModel.backPresed() })
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Text(
                text = "¿Qué quieres hacer?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            Button(
                onClick = { viewModel.onClickAudiolibros() },
                modifier = Modifier.padding(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                ),
                border = BorderStroke(2.dp, Color.Blue)
            ) {
                Text("📚 AUDIOLIBROS", Modifier.padding(10.dp))
            }
            
            Button(
                onClick = { viewModel.onClickJuegos() },
                modifier = Modifier.padding(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                ),
                border = BorderStroke(2.dp, Color.Green)
            ) {
                Text("🎮 JUEGOS", Modifier.padding(10.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OpcionesPreview() {
    MisLecturitasTheme {
        OpcionesActivityComposable(viewModel = OpcionesViewModel())
    }
}
