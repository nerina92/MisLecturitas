package edu.mis.lecturitas.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme

class AdminMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminMainScreen(
                        onBackClick = { finish() },
                        onSalaDe3Click = { openManageContentActivity(3) },
                        onSalaDe4Click = { openManageContentActivity(4) },
                        onSalaDe5Click = { openManageContentActivity(5) },
                        onAddContentClick = { openAddContentActivity() }
                    )
                }
            }
        }
    }
    
    private fun openManageContentActivity(nivel: Int) {
        val intent = Intent(this, ManageContentActivity::class.java).apply {
            putExtra("nivel", nivel)
            putExtra("contentType", "libro") // Por defecto mostrar libros
        }
        startActivity(intent)
    }
    
    private fun openAddContentActivity() {
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    onBackClick: () -> Unit,
    onSalaDe3Click: () -> Unit,
    onSalaDe4Click: () -> Unit,
    onSalaDe5Click: () -> Unit,
    onAddContentClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Toolbar
        MyToolbar(onClickBack = onBackClick)
        
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Ilustración de administración
            Image(
                painter = painterResource(id = R.drawable.admin_illustration), // Necesitarás crear este drawable
                contentDescription = "Administración",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            
                // Botón para agregar contenido
                Button(
                    onClick = onAddContentClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20B2AA)), // Teal
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "➕ AGREGAR CONTENIDO",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Botones de salas
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Sala de 3
                Button(
                    onClick = onSalaDe3Click,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E)), // Rojo
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SALA DE 3",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Sala de 4
                OutlinedButton(
                    onClick = onSalaDe4Click,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53E3E)
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE53E3E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SALA DE 4",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Sala de 5
                Button(
                    onClick = onSalaDe5Click,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3748)), // Azul oscuro
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SALA DE 5",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
