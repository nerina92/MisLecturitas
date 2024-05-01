package edu.mis.lecturitas.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.sp
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.FilledButton
import edu.mis.lecturitas.ui.login.LoginViewModel
import edu.mis.lecturitas.ui.main.ui.theme.MisLecturitasTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

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
    }
}

@Composable
fun MainActivityComposable(viewModel: MainViewModel) {
    val image = painterResource(R.drawable.mis_lecturitas_download)
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        FilledButton(onClick = { viewModel.onClickSalaDe3()}, text = "SALA DE 3", color = Color.Red, borderColor = Color.Red )
        FilledButton(onClick = { viewModel.onClickSalaDe4() }, text = "SALA DE 4", color = Color.Red, borderColor = Color.Blue )
        FilledButton(onClick = { viewModel.onClickSalaDe5() }, text = "SALA DE 5", color = Color.Blue, borderColor = Color.Blue )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MisLecturitasTheme {
        MainActivityComposable(viewModel = MainViewModel())
    }
}