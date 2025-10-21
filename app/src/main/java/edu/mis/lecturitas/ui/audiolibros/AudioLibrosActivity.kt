package edu.mis.lecturitas.ui.audiolibros

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import edu.mis.lecturitas.R
import edu.mis.lecturitas.model.AudioLibro
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioLibrosActivity : ComponentActivity() {
    private val viewModel: AudioLibrosViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Obtener el nivel desde el intent, si no se proporciona, mostrar todos
        val nivel = intent.getIntExtra("nivel", -1)
        
        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AudioLibrosActivityComposable(viewModel)
                }
            }
        }
        
        // Cargar audiolibros según el nivel
        if (nivel == -1) {
            viewModel.consultarTodosLosAudioLibros()
        } else {
            viewModel.consultarAudioLibros(nivel)
        }
        
        viewModel.goBack.observe(this) {
            if (it) {
                onBackPressed()
                viewModel.doneGoBack()
            }
        }
        
        viewModel.openAudioLibro.observe(this) {
            if (it != null) {
                openVideoPlayer(it)
                viewModel.setOpenAudioLibroNull()
            }
        }
    }
    
    private fun openVideoPlayer(audioLibro: AudioLibro) {
        val intent = Intent(this, VideoPlayerActivity::class.java)
        intent.putExtra("audioLibro", audioLibro)
        startActivity(intent)
    }
}

@Composable
fun AudioLibrosActivityComposable(viewModel: AudioLibrosViewModel) {
    val listaAudioLibros by viewModel.listaAudioLibros.observeAsState(emptyList())
    val showProgressBar by viewModel.showProgressBar.observeAsState(false)
    
    Column {
        MyToolbar({ viewModel.backPresed() })
        
        Text(
            text = "Audiolibros con Video",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
        
        if (showProgressBar) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (listaAudioLibros.isNullOrEmpty() ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay audiolibros disponibles",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(listaAudioLibros!!) { audioLibro ->
                        AudioLibroCard(
                            audioLibro = audioLibro,
                            onClick = { viewModel.onClickAudioLibro(audioLibro) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioLibroCard(
    audioLibro: AudioLibro,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de portada
            Image(
                painter = if (audioLibro.urlImagen.isNotEmpty()) {
                    rememberImagePainter(audioLibro.urlImagen)
                } else {
                    painterResource(R.drawable.dibujo5)
                },
                contentDescription = audioLibro.titulo,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del audiolibro
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = audioLibro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Por: ${audioLibro.autor}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Duración: ${audioLibro.duracion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (audioLibro.descripcion.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = audioLibro.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AudioLibroCardPreview() {
    MisLecturitasTheme {
        AudioLibroCard(
            audioLibro = AudioLibro(
                idAudioLibro = 1,
                titulo = "El Cuento de la Princesa",
                descripcion = "Una hermosa historia contada por los niños del jardín",
                urlVideo = "",
                urlImagen = "",
                duracion = "5:30",
                nivel = 4,
                fechaCreacion = "2024-01-15",
                autor = "Grupo de Sala de 4",
                estado = "activo",
                cantReproducciones = 10,
                calificacion = 5
            ),
            onClick = {}
        )
    }
}
