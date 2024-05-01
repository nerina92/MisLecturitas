package edu.mis.lecturitas.ui.bookList

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import edu.mis.lecturitas.R
import edu.mis.lecturitas.ui.bookList.ui.theme.MisLecturitasTheme

class BookListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisLecturitasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   // Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun BookItem(name: String, image: Painter) {

}

@Preview(showBackground = true)
@Composable
fun BookItemPreview2() {
    MisLecturitasTheme {
        BookItem("Android", painterResource(R.drawable.mis_lecturitas_download))

    }
}