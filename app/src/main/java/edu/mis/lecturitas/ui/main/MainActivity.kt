package edu.mis.lecturitas.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import edu.mis.lecturitas.ui.bookList.BookListActivity
import edu.mis.lecturitas.ui.opciones.OpcionesActivity
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisLecturitasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityComposable(viewModel)
                }
            }
        }
        viewModel.openListBook.observe(this){
            if(it!=null) {
                openBookListActivity(it)

                viewModel.setOpenListBookNull()
            }
        }
        viewModel.goBack.observe(this){
            if(it) {
                onBackPressed()
                viewModel.doneGoback()
            }
        }
        
        viewModel.openOpciones.observe(this){
            if(it) {
                openOpcionesActivity()
                viewModel.setOpenOpcionesFalse()
            }
        }
    }
    fun openBookListActivity(nivel:Int){
        val i = Intent(this, BookListActivity::class.java)
        i.putExtra("nivel", nivel)
        startActivity(i)
    }
    
    fun openOpcionesActivity(){
        val i = Intent(this, OpcionesActivity::class.java)
        startActivity(i)
    }
}

@Composable
fun MainActivityComposable(viewModel: MainViewModel) {
    val image = painterResource(R.drawable.dibujo5)
    var showSala5Options = remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
Column{
    MyToolbar({ viewModel.backPresed() })
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        //FilledButton(onClick = { viewModel.onClickSalaDe3()}, text = "SALA DE 3", color = Color.Red, borderColor = Color.Red)
        //FilledButton(onClick = { viewModel.onClickSalaDe4() }, text = "SALA DE 4", color = Color.Red, borderColor = Color.Blue )
        //FilledButton(onClick = { viewModel.onClickSalaDe5() }, text = "SALA DE 5", color = Color.Blue, borderColor = Color.Blue )
        Button(onClick = { viewModel.onClickSalaDe3() },
            Modifier.padding(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
            ),
            border = BorderStroke(1.dp, Color.Red)
        ) {
            Text("   SALA DE 3   ",Modifier.padding(10.dp),)
        }
        Button(onClick = { viewModel.onClickSalaDe4() },
            Modifier.padding(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
            ),
            border = BorderStroke(4.dp, Color.Blue)
        ) {
            Text("   SALA DE 4   ",Modifier.padding(10.dp),)
        }
        Button(onClick = { viewModel.onClickSalaDe5() },
            Modifier.padding(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
            ),
            border = BorderStroke(1.dp, Color.Blue)
        ) {
            Text("   SALA DE 5   ",Modifier.padding(10.dp),)
        }


        Button(onClick = { viewModel.onClickJugar() },
            Modifier.padding(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
            ),
            border = BorderStroke(1.dp, Color.Magenta)
        ) {
            Text("\uD83C\uDFB2 MODO INTERACTIVO ",Modifier.padding(10.dp),)
        }


            showSala5Options.value=true
        }
        if (showSala5Options.value) {
            Button(
                onClick = {
                    showSala5Options.value = false
                    viewModel.onClickSalaDe5Play()
                },
                Modifier.padding(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                border = BorderStroke(1.dp, Color.Green)
            ) {
                Text("JUGAR", Modifier.padding(10.dp))
            }
            Button(
                onClick = {
                    showSala5Options.value = false
                    viewModel.onClickSalaDe5Read()
                },
                Modifier.padding(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                border = BorderStroke(1.dp, Color.Yellow)
            ) {
                Text("LEER", Modifier.padding(10.dp))
            }
        }
    }
}

    }


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MisLecturitasTheme {
        MainActivityComposable(viewModel = MainViewModel())
    }
}
