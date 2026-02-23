package edu.mis.lecturitas.ui.juegos

import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import edu.mis.lecturitas.R
import java.util.*
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.modifier.modifierLocalConsumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import edu.mis.lecturitas.repository.GamificationRepository
import edu.mis.lecturitas.repository.UserRepository
import edu.mis.lecturitas.utils.ActivityType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

// Función para cargar imagen desde URL
suspend fun loadImageFromUrl(url: String, context: android.content.Context): ImageBitmap? {
    return try {
        withContext(Dispatchers.IO) {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.drawable
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    Log.d("ImageLoading", "Imagen cargada exitosamente desde: $url")
                    bitmap.asImageBitmap()
                } else {
                    Log.w("ImageLoading", "Drawable no es BitmapDrawable")
                    null
                }
            } else {
                Log.w("ImageLoading", "Error en la carga de la imagen")
                null
            }
        }
    } catch (e: Exception) {
        Log.e("ImageLoading", "Error loading image from URL: $url", e)
        null
    }
}

class RompecabezasActivity : ComponentActivity() {
    companion object {
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_IMAGE = "EXTRA_IMAGE"
    }

    private var tts: TextToSpeech? = null
    private lateinit var mpCorrect: MediaPlayer
    private val gamificationRepository = GamificationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
            }
        }
        mpCorrect = MediaPlayer.create(this, R.raw.correcto)

        // Obtener URL e imagen de los extras
        val urlRecibida: String? = intent.getStringExtra("EXTRA_URL")
        val imagenRecibida: String? = intent.getStringExtra("EXTRA_IMAGE")

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PuzzleScreen(
                        imageUrl = imagenRecibida,
                        onSpeakerClick = {
                            tts?.speak(
                                "Toca dos piezas para intercambiarlas y arma el rompecabezas. Puedes cambiar la dificultad.",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        },
                        onComplete = {
                            mpCorrect.start()
                            // Registrar actividad de juego completado
                            recordGameActivity()
                        }
                    )
                }
            }
        }
    }

    private fun recordGameActivity() {
        val currentUser = UserRepository.getCurrentUser()
        if (currentUser != null && !currentUser.user.equals("invitado", ignoreCase = true)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Obtener progreso actual del usuario
                    val currentProgress = gamificationRepository.getUserProgress(currentUser.user).first()

                    if (currentProgress != null) {
                        gamificationRepository.recordActivity(
                            userId = currentUser.user,
                            activityType = ActivityType.GAME_COMPLETED,
                            currentProgress = currentProgress
                        )
                        Log.d("Gamification", "Juego registrado para usuario: ${currentUser.user}")
                    } else {
                        // Inicializar progreso si no existe
                        gamificationRepository.initializeUserProgress(currentUser.user)
                        val newProgress = gamificationRepository.getUserProgress(currentUser.user).first()
                        if (newProgress != null) {
                            gamificationRepository.recordActivity(
                                userId = currentUser.user,
                                activityType = ActivityType.GAME_COMPLETED,
                                currentProgress = newProgress
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Gamification", "Error al registrar juego", e)
                }
            }
        } else {
            Log.d("Gamification", "Usuario invitado - no se registra actividad")
        }
    }
    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun PuzzleScreen(
    imageUrl: String? = null,
    onSpeakerClick: () -> Unit,
    onComplete: () -> Unit
) {
    val ctx = LocalContext.current
    var gridSize by remember { mutableStateOf(2) }
    var expanded by remember { mutableStateOf(false) }

    // Imagen por defecto
    val defaultImg = ImageBitmap.imageResource(R.drawable.puzzle)

    // Estado para manejar la carga de imagen
    var loadedImage by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }
    var isLoadingImage by remember(imageUrl) { mutableStateOf(false) }

    // Cargar imagen desde URL si está disponible
    LaunchedEffect(imageUrl) {
        if (imageUrl != null && imageUrl.isNotBlank()) {
            isLoadingImage = true
            try {
                val result = loadImageFromUrl(imageUrl, ctx)
                loadedImage = result
                Log.d("PuzzleScreen", "Imagen cargada: ${result != null}")
            } catch (e: Exception) {
                Log.e("PuzzleScreen", "Error al cargar imagen", e)
                loadedImage = null
            } finally {
                isLoadingImage = false
            }
        } else {
            loadedImage = null
            isLoadingImage = false
        }
    }

    // Usar la imagen cargada o la imagen por defecto
    val img = loadedImage ?: defaultImg

    val slices = remember(gridSize, img) { sliceNxN(img, gridSize) }
    val totalTiles = gridSize * gridSize
    var order by remember { mutableStateOf((0 until totalTiles).toList().shuffled()) }
    var selected by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rompecabezas ${gridSize}×${gridSize}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mostrar indicador de carga si se está cargando la imagen
            if (isLoadingImage) {
                Text(
                    text = "Cargando imagen del cuento...",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "Toca dos piezas para intercambiarlas.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(modifier = Modifier.fillMaxWidth(0.5f), onClick = onSpeakerClick) {
                    Text(" 🔊 Escuchar instrucciones", textAlign = TextAlign.Center)
                    //Icon(Icons.Default.Notifications, contentDescription = "Instrucciones")
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Selector de dificultad
                Box {
                    Button(modifier = Modifier.fillMaxWidth(1f), onClick = { expanded = true }) {
                        Text("⚙️ Dificultad: ${gridSize}x${gridSize}")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf(2, 3, 4).forEach { size ->
                            DropdownMenuItem(
                                //icono configuración

                                text = { Text("${size}x${size}") },
                                onClick = {
                                    gridSize = size
                                    order = (0 until size * size).toList().shuffled()
                                    selected = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }


        PuzzleBoard(
            order = order,
            slices = slices,
            gridSize = gridSize,
            selectedIndex = selected,
//            tileSize = (300.dp / gridSize),
            onTileClick = { gridIndex ->
                if (selected == null) {
                    selected = gridIndex
                } else {
                    val a = selected!!
                    val b = gridIndex
                    if (a != b) {
                        val mutable = order.toMutableList()
                        val tmp = mutable[a]
                        mutable[a] = mutable[b]
                        mutable[b] = tmp
                        order = mutable.toList()
                        if (isSolved(order)) {
                            Toast.makeText(ctx, "¡Completado!", Toast.LENGTH_LONG).show()
                            onComplete()
                        }
                    }
                    selected = null
                }
            }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                order = (0 until totalTiles).toList().shuffled()
                selected = null
            }) { Text("Mezclar") }
            Button(onClick = {
                order = (0 until totalTiles).toList()
                selected = null
            }) { Text("Resolver") }
        }

    }
}


/*@Composable
fun PuzzleBoard(
    order: List<Int>,
    slices: List<ImageBitmap>?,
    gridSize: Int,
    //tileSize: Dp,
    selectedIndex: Int?,
    onTileClick: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        for (row in 0 until gridSize) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (col in 0 until gridSize) {
                    val gridIndex = row * gridSize + col
                    val tileId = order[gridIndex]
                    PuzzleTile(
                        tileId = tileId,
                        image = slices?.getOrNull(tileId),
                        tileSize = tileSize,
                        selected = (selectedIndex == gridIndex),
                        onClick = { onTileClick(gridIndex) }
                    )
                }
            }
        }
    }
}*/
@Composable
fun PuzzleBoard(
    order: List<Int>,
    slices: List<ImageBitmap>?,
    gridSize: Int,
    selectedIndex: Int?,
    onTileClick: (Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val espacio = 2.dp
        val totalEspacio = espacio * (gridSize - 1)
        val tileSize = (maxWidth - totalEspacio) / gridSize // <-- usa maxWidth aquí
        Column(verticalArrangement = Arrangement.spacedBy(espacio)) {
            for (row in 0 until gridSize) {
                Row(horizontalArrangement = Arrangement.spacedBy(espacio)) {
                    for (col in 0 until gridSize) {
                        val gridIndex = row * gridSize + col
                        val tileId = order[gridIndex]
                        PuzzleTile(
                            tileId = tileId,
                            image = slices?.getOrNull(tileId),
                            tileSize = tileSize,
                            selected = (selectedIndex == gridIndex),
                            onClick = { onTileClick(gridIndex) }
                        )
                    }
                }
            }
        }
    }
}



fun isSolved(order: List<Int>): Boolean =
    order == (0 until order.size).toList()

fun sliceNxN(image: ImageBitmap, n: Int): List<ImageBitmap> {
    val src = image.asAndroidBitmap()
    val w = src.width
    val h = src.height
    val tw = w / n
    val th = h / n
    val result = mutableListOf<ImageBitmap>()
    for (row in 0 until n) {
        for (col in 0 until n) {
            val x = col * tw
            val y = row * th
            val bmp = android.graphics.Bitmap.createBitmap(src, x, y, tw, th)
            result.add(bmp.asImageBitmap())
        }
    }
    return result
}

@Composable
fun PuzzleTile(
    tileId: Int,
    image: ImageBitmap?,
    tileSize: Dp,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val border = if (selected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, Color.LightGray)
    Card(
        modifier = Modifier
            .size(tileSize)
            .clip(shape)
            .border(border, shape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (image != null) {
                Image(bitmap = image, contentDescription = "pieza ${tileId + 1}", modifier = Modifier.fillMaxSize())
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFECECEC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${tileId + 1}", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
            }
        }
    }
}
