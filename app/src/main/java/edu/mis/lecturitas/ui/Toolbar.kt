package edu.mis.lecturitas.ui

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyToolbar(onClickBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF18162)),
        title = {
            Text(text = "Mis lecturitas", color = Color.White)
        },
       // backgroundColor = Color.Blue,
        //contentColor = Color.White,
        //elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = { onClickBack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
           /* IconButton(onClick = { *//* Handle action 1 click *//* }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }*/
            IconButton(onClick = { /* Handle action 2 click */ }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
            }
        },

    )
}