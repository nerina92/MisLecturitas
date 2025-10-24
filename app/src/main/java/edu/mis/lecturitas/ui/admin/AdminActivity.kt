package edu.mis.lecturitas.ui.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminActivity : ComponentActivity() {
    private val viewModel: AdminViewModel by viewModel()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido
        } else {
            Toast.makeText(this, "Permiso necesario para acceder a archivos", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setPdfUri(uri)
            }
        }
    }
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setImageUri(uri)
            }
        }
    }
    
    private val videoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setVideoUri(uri)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        // Obtener datos de edición si vienen del intent
        val editData = intent.getSerializableExtra("editData")
        val contentType = intent.getStringExtra("contentType") ?: "libro"
        val nivel = intent.getIntExtra("nivel", 3)
        
        if (editData != null) {
            viewModel.setEditMode(editData, contentType, nivel)
        }
        
        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminScreen(
                        viewModel = viewModel,
                        onBackClick = { finish() },
                        onPdfClick = { openPdfPicker() },
                        onVideoClick = { openVideoPicker() },
                        onImageClick = { openImagePicker() },
                        onPublishClick = { viewModel.publishContent() }
                    )
                }
            }
        }
        
        // Observar cambios en el ViewModel usando StateFlow
        lifecycleScope.launch {
            viewModel.showMessage.collect { message ->
                if (message.isNotEmpty()) {
                    Toast.makeText(this@AdminActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.isPublished.collect { isPublished ->
                if (isPublished) {
                    Toast.makeText(this@AdminActivity, "Contenido publicado exitosamente", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
    
    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
        }
        pdfPickerLauncher.launch(Intent.createChooser(intent, "Seleccionar PDF"))
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(Intent.createChooser(intent, "Seleccionar imagen"))
    }
    
    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*"
        }
        videoPickerLauncher.launch(Intent.createChooser(intent, "Seleccionar video"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onBackClick: () -> Unit,
    onPdfClick: () -> Unit,
    onVideoClick: () -> Unit,
    onImageClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Toolbar
        MyToolbar(onClickBack = onBackClick)
        
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = "Cargar contenido",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Tipo de contenido
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TIPO DE CONTENIDO",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            onClick = { viewModel.setContentType(ContentType.LIBRO) },
                            label = { Text("Libro") },
                            selected = uiState.contentType == ContentType.LIBRO,
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            onClick = { viewModel.setContentType(ContentType.AUDIOLIBRO) },
                            label = { Text("Audiolibro") },
                            selected = uiState.contentType == ContentType.AUDIOLIBRO,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Campos de texto
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = viewModel::setTitulo,
                label = { Text("TÍTULO") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.autor,
                onValueChange = viewModel::setAutor,
                label = { Text("AUTOR") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            if (uiState.contentType == ContentType.LIBRO) {
                OutlinedTextField(
                    value = uiState.urlPdf,
                    onValueChange = viewModel::setUrlPdf,
                    label = { Text("URL ARCHIVO PDF") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                OutlinedTextField(
                    value = uiState.descripcion,
                    onValueChange = viewModel::setDescripcion,
                    label = { Text("DESCRIPCIÓN") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
            
            // Nivel educativo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NIVEL EDUCATIVO",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            onClick = { viewModel.setNivel(3) },
                            label = { Text("Sala de 3") },
                            selected = uiState.nivel == 3,
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            onClick = { viewModel.setNivel(4) },
                            label = { Text("Sala de 4") },
                            selected = uiState.nivel == 4,
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            onClick = { viewModel.setNivel(5) },
                            label = { Text("Sala de 5") },
                            selected = uiState.nivel == 5,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Subida de archivos
            if (uiState.contentType == ContentType.LIBRO) {
                // Subir PDF
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ARCHIVO PDF",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Button(
                            onClick = onPdfClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cargar PDF")
                        }
                        
                        if (uiState.pdfFileName.isNotEmpty()) {
                            Text(
                                text = "Archivo seleccionado: ${uiState.pdfFileName}",
                                fontSize = 12.sp,
                                color = Color.Green
                            )
                        }
                    }
                }
            } else {
                // Subir video para audiolibro
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ARCHIVO VIDEO",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Button(
                            onClick = onVideoClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cargar Video")
                        }
                        
                        if (uiState.videoFileName.isNotEmpty()) {
                            Text(
                                text = "Archivo seleccionado: ${uiState.videoFileName}",
                                fontSize = 12.sp,
                                color = Color.Green
                            )
                        }
                    }
                }
            }
            
            // Subir imagen de portada
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "IMAGEN TAPA",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Preview de imagen
                    if (uiState.imageUri != null) {
                        Image(
                            painter = rememberImagePainter(uiState.imageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.White)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .clickable { onImageClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Toca para seleccionar imagen",
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Button(
                        onClick = onImageClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cargar Imagen")
                    }
                }
            }
            
            // Botón publicar
            Button(
                onClick = onPublishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = uiState.isValid && !uiState.isUploading
            ) {
                if (uiState.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (uiState.isUploading) "Publicando..." else "Publicar",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
