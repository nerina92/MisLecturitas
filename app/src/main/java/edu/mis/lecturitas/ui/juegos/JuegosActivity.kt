package edu.mis.lecturitas.ui.juegos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class JuegosActivity : ComponentActivity() {
    private val viewModel: JuegosViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JuegosActivityComposable(viewModel, { openPuzzleActivity() })
                }
            }
        }
        
        viewModel.goBack.observe(this) {
            if (it) {
                onBackPressed()
                viewModel.doneGoback()
            }
        }
        
        viewModel.openFormas.observe(this) {
            if (it) {
                openFormasActivity()
                viewModel.setOpenFormasFalse()
            }
        }


    }
    
    private fun openFormasActivity() {
        val intent = Intent(this, FormasActivity::class.java)
        startActivity(intent)
    }

    private fun openPuzzleActivity() {
        val intent = Intent(this, RompecabezasActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun JuegosActivityComposable(viewModel: JuegosViewModel, openPuzzleActivity: () -> Unit) {
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
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxHeight(0.35f)
            )
            
            Text(
                text = "Selecciona un juego",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            Button(
                onClick = { viewModel.onClickFormas() },
                modifier = Modifier.padding(30.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text("🔺 FORMAS Y FIGURAS", Modifier.padding(10.dp))
            }

            Button(
                onClick = { openPuzzleActivity() },
                modifier = Modifier.padding(30.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text(" \uD83E\uDDE9 PUZZLE  ", Modifier.padding(10.dp))
            }
            
            // Aquí se pueden agregar más juegos en el futuro
            Text(
                text = "Más juegos próximamente...",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun JuegosPreview() {
    MisLecturitasTheme {
        JuegosActivityComposable(viewModel = JuegosViewModel(),{})
    }
}
