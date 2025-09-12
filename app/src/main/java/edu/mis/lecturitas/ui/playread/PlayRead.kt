package edu.mis.lecturitas.ui.playread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.mis.lecturitas.ui.MyToolbar

@Composable
fun PlayRead(modifier: Modifier = Modifier, url: String? = null) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MyToolbar({ /*viewModel.backPresed()*/ })
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Button(onClick = { /* Acción para el primer botón */ }) {
                Text("JUGAR")
            }
            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los botones
            Button(onClick = { /* Acción para el segundo botón */ }) {
                Text("LEER")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlayRead()
}
