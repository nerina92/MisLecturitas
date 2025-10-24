package edu.mis.lecturitas.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.mis.lecturitas.R
import edu.mis.lecturitas.model.AudioLibro
import edu.mis.lecturitas.model.Libro
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class ManageContentActivity : ComponentActivity() {
    private val viewModel: ManageContentViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val nivel = intent.getIntExtra("nivel", 3)
        val contentType = intent.getStringExtra("contentType") ?: "libro"
        
        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ManageContentScreen(
                        viewModel = viewModel,
                        nivel = nivel,
                        contentType = contentType,
                        onBackClick = { finish() },
                        onAddClick = { 
                            val intent = Intent(this, AdminActivity::class.java).apply {
                                putExtra("nivel", nivel)
                                putExtra("contentType", contentType)
                            }
                            startActivity(intent)
                        },
                        onEditClick = { content ->
                            val intent = Intent(this, AdminActivity::class.java).apply {
                                putExtra("editData", content)
                                putExtra("contentType", contentType)
                                putExtra("nivel", nivel)
                            }
                            startActivity(intent)
                        },
                        onDeleteClick = { content ->
                            viewModel.deleteContent(content)
                        },
                        onSwitchToLibros = {
                            val intent = Intent(this, ManageContentActivity::class.java).apply {
                                putExtra("nivel", nivel)
                                putExtra("contentType", "libro")
                            }
                            startActivity(intent)
                            finish()
                        },
                        onSwitchToAudiolibros = {
                            val intent = Intent(this, ManageContentActivity::class.java).apply {
                                putExtra("nivel", nivel)
                                putExtra("contentType", "audiolibro")
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
        
        // Cargar contenido
        viewModel.loadContent(nivel, contentType)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageContentScreen(
    viewModel: ManageContentViewModel,
    nivel: Int,
    contentType: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Serializable) -> Unit,
    onDeleteClick: (Any) -> Unit,
    onSwitchToLibros: () -> Unit,
    onSwitchToAudiolibros: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Toolbar
        MyToolbar(
            //title = "SALA DE $nivel",
            onClickBack = onBackClick
        )
        
        // Selector de tipo de contenido
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = onSwitchToLibros,
                    label = { Text("Libros") },
                    selected = contentType == "libro",
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    onClick = onSwitchToAudiolibros,
                    label = { Text("Audiolibros") },
                    selected = contentType == "audiolibro",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Lista de contenido
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (contentType) {
                    "libro" -> {
                        items(uiState.libros) { libro ->
                            ContentItem(
                                title = libro.nombre,
                                subtitle = libro.autor,
                                imageUrl = libro.imagen,
                                onEditClick = { onEditClick(libro) },
                                onDeleteClick = { onDeleteClick(libro) }
                            )
                        }
                    }
                    "audiolibro" -> {
                        items(uiState.audioLibros) { audioLibro ->
                            ContentItem(
                                title = audioLibro.titulo,
                                subtitle = audioLibro.autor,
                                imageUrl = audioLibro.urlImagen,
                                onEditClick = { onEditClick(audioLibro) },
                                onDeleteClick = { onDeleteClick(audioLibro) }
                            )
                        }
                    }
                }
            }
            
            // Floating Action Button
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFF20B2AA) // Teal
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ContentItem(
    title: String,
    subtitle: String,
    imageUrl: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B6B)), // Coral
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen circular
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Texto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
            
            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
