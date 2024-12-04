package com.example.scanmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.scanmaster.ui.theme.ScanMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ScanMasterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Scaffold(
        bottomBar = { BottomControls() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .align(Alignment.Center)
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Captured text content will \n be displayed here",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.lavender_gray)
                )
            }
        }
    }
}

@Composable
fun BottomControls() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(colorResource(id = R.color.lavender_gray)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        GifIconButton(
            gifRes = R.drawable.delete_text,
            contentDescription = "Erase",
            iconSize = 200.dp
        )

        GifIconButton(
            gifRes = R.drawable.camera_icon,
            contentDescription = "Camera",
            iconSize = 200.dp
        )

        GifIconButton(
            gifRes = R.drawable.copy_icon,
            contentDescription = "Copy",
            iconSize = 45.dp
        )
    }
}

@Composable
fun GifIconButton(
    gifRes: Int,
    contentDescription: String,
    iconSize: Dp = 40.dp,
    buttonSize: Dp = 60.dp
) {
    Box(
        modifier = Modifier
            .size(buttonSize),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(gifRes)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

