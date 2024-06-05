package edu.mis.lecturitas.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.mis.lecturitas.R
import edu.mis.lecturitas.model.Usuario

import edu.mis.lecturitas.ui.login.ui.theme.MisLecturitasTheme
import edu.mis.lecturitas.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisLecturitasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Login(viewModel, getAppVersion(this))
                }
            }
        }
        viewModel.resultadoLogin.observe(this) {
            if (it != null) {
                if (it.exito) {
                    Log.d("Login OK", "Abro main activity")
                    openMainActivity()
                }else{
                    Log.d("Error login", "Error login : ${it.mensaje}")
                    Toast.makeText(this, it.mensaje, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getAppVersion(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "N/A"
    }

    fun openMainActivity(){
        Log.d("Abro main", "Abro main activity 2")
        val i = Intent(this, MainActivity::class.java)
        //i.putExtra("usuario", viewModel.usuarioLogueado.value)
        startActivity(i)
        finish()
    }
}



@Composable
fun Login(viewModel: LoginViewModel, appVersion: String) {
    val image = painterResource(R.drawable.mis_lecturitas_download)

    var text by remember { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MIS LECTURITAS",
            fontFamily = FontFamily(Font(R.font.league_spartan_bold)),
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1C2120),
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .padding(1.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                fontFamily = FontFamily(Font(R.font.league_spartan_bold)),
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1C2120),
                modifier = Modifier
                    .padding(5.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Text(
                text = "Inicie sesión para continuar",
                fontFamily = FontFamily(Font(R.font.league_spartan_light)),
                    fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8f8e8e),
                modifier = Modifier
                    .padding(5.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
        }
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Usuario") },
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            FilledTonalButton(
                onClick = {
                    viewModel.consultarUsuario(
                        Usuario( text.trim(),password)
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    "Ingresar",
                    color = Color(0xFFFFffff),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 1.dp)
                )
            }
        }


        Text(
            text = "App - v $appVersion",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF686868),
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
    }
}