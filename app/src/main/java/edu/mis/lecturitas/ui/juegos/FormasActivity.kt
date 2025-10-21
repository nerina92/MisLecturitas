package edu.mis.lecturitas.ui.juegos

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.MyToolbar
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import java.util.Locale

class FormasActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var mpCorrect: MediaPlayer
    private lateinit var mpWrong: MediaPlayer
    private var ttsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar TTS y sonidos
        tts = TextToSpeech(this, this)
        // Usar sonidos del sistema por ahora, más adelante se pueden agregar archivos de audio personalizados
        mpCorrect = MediaPlayer.create(this, R.raw.correcto)
        mpWrong = MediaPlayer.create(this, R.raw.incorrecto)

        setContent {
            MisLecturitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FormasActivityComposable(
                        speak = { sayShape(it) },
                        playCorrect = { mpCorrect.start() },
                        playWrong = { mpWrong.start() },
                        onBackPressed = { onBackPressed() }
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
            ttsInitialized = true
        }
    }

    private  fun sayShape(shape: String) {
        println("Leyendo forma: $shape")

        if (ttsInitialized) {
            val text = when (shape) {
                "circle" -> "Encuentra el círculo"
                "square" -> "Encuentra el cuadrado"
                "triangle" -> "Encuentra el triángulo"
                else -> ""
            }
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }else{
            println("TTS no está inicializado aún.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        mpCorrect.release()
        mpWrong.release()
    }
}

@Composable
fun FormasActivityComposable(
    speak: (String) -> Unit,
    playCorrect: () -> Unit,
    playWrong: () -> Unit,
    onBackPressed: () -> Unit
) {
    var currentShape by remember { mutableStateOf("circle") }
    var message by remember { mutableStateOf("¡Encuentra la forma!") }
    var showCorrectMessage by remember { mutableStateOf(false) }
    var shouldSpeakAgain by remember { mutableStateOf(false) }

    // Función para obtener el nombre de la forma en español
    fun getShapeName(shape: String): String {
        return when (shape) {
            "circle" -> "CÍRCULO"
            "square" -> "CUADRADO"
            "triangle" -> "TRIÁNGULO"
            else -> ""
        }
    }

    // Función para manejar respuesta correcta
    fun handleCorrectAnswer(shapeName: String) {
        playCorrect()
        message = "¡Excelente! Encontraste el $shapeName"
        showCorrectMessage = true
    }

    // Al iniciar, que hable la forma (esperando a que TTS esté listo)
    LaunchedEffect(currentShape) {
        // Esperar un poco para asegurar que TTS esté inicializado
        delay(1000)
        speak(currentShape)
    }

    // Manejar el delay después de una respuesta correcta
    LaunchedEffect(showCorrectMessage) {
        if (showCorrectMessage) {
            delay(2000) // 2 segundos
            currentShape = listOf("circle", "square", "triangle").random()
            message = "¡Encuentra la forma!"
            showCorrectMessage = false
        }
    }

    // Manejar el botón "Escuchar de nuevo"
    LaunchedEffect(shouldSpeakAgain) {
        if (shouldSpeakAgain) {
            delay(100)
            speak(currentShape)
            shouldSpeakAgain = false
        }
    }

    Column {
        MyToolbar({
            // Manejar back press
             onBackPressed()

        })
        
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Juego de Formas",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
            
            // Mostrar claramente qué forma debe encontrar
            Text(
                text = "Encuentra el:",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(8.dp)
            )
            
            Text(
                text = getShapeName(currentShape),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
            
            Text(
                text = message, 
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Botón para escuchar nuevamente la instrucción
            Button(
                onClick = { 
                    shouldSpeakAgain = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("🔊 Escuchar de nuevo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila con las 3 formas
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ShapeItem(R.drawable.circulo, "Circulo", currentShape, onResult = { correct ->
                    if (correct) {
                        handleCorrectAnswer("círculo")
                    } else {
                        playWrong()
                        message = "Esa no es la forma correcta. ¡Sigue intentando!"
                    }
                })

                ShapeItem(R.drawable.cuadrado, "Cuadrado", currentShape, onResult = { correct ->
                    if (correct) {
                        handleCorrectAnswer("cuadrado")
                    } else {
                        playWrong()
                        message = "Esa no es la forma correcta. ¡Sigue intentando!"
                    }
                })

                ShapeItem(R.drawable.triangulo, "Triangulo", currentShape, onResult = { correct ->
                    if (correct) {
                        handleCorrectAnswer("triángulo")
                    } else {
                        playWrong()
                        message = "Esa no es la forma correcta. ¡Sigue intentando!"
                    }
                })
            }
        }
    }
}

@Composable
fun ShapeItem(
    drawableId: Int,
    shapeName: String,
    currentShape: String,
    onResult: (Boolean) -> Unit
) {
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = shapeName,
        modifier = Modifier
            .size(120.dp)
            .clickable {
                val isCorrect = when (currentShape) {
                    "circle" -> shapeName == "Circulo"
                    "square" -> shapeName == "Cuadrado"
                    "triangle" -> shapeName == "Triangulo"
                    else -> false
                }
                onResult(isCorrect)
            }
    )
}
