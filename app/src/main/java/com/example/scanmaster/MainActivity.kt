package com.example.scanmaster

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.scanmaster.ui.theme.ScanMasterTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.app.Activity
import androidx.compose.foundation.clickable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var detectedText by remember { mutableStateOf("Captured text content will be displayed here") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val extras = result.data?.extras
            val bitmap = extras?.get("data") as? Bitmap
            if (bitmap != null) {
                capturedBitmap = bitmap
                detectTextUsingML(
                    bitmap,
                    onTextDetected = { detectedText = it },
                    onFailure = { Toast.makeText(context, "Text detection failed", Toast.LENGTH_SHORT).show() }
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomControls(
                onEraseClick = {
                    capturedBitmap = null
                    detectedText = "Captured text content will be displayed here"
                },
                onCopyClick = {
                    if (detectedText.isNotEmpty()) {
                        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()

                    }
                },
                onCameraClick = {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(intent)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (capturedBitmap != null) {
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp)
                    )
                }
                Text(
                    text = detectedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(id = R.color.lavender_gray)
                )
            }
        }
    }
}

@Composable
fun BottomControls(
    onEraseClick: () -> Unit,
    onCopyClick: () -> Unit,
    onCameraClick: () -> Unit
) {
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
            onClick = onEraseClick
        )

        GifIconButton(
            gifRes = R.drawable.camera_icon,
            contentDescription = "Camera",
            onClick = onCameraClick
        )

        GifIconButton(
            gifRes = R.drawable.copy_icon,
            contentDescription = "Copy",
            onClick = onCopyClick
        )
    }
}

@Composable
fun GifIconButton(
    gifRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    iconSize: Dp = 40.dp,
    buttonSize: Dp = 60.dp
) {
    Box(
        modifier = Modifier
            .size(buttonSize)
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .wrapContentSize(Alignment.Center)
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() },
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

fun detectTextUsingML(
    bitmap: Bitmap,
    onTextDetected: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val inputImage = InputImage.fromBitmap(bitmap, 0)

    recognizer.process(inputImage)
        .addOnSuccessListener { visionText ->
            onTextDetected(visionText.text)
        }
        .addOnFailureListener { e ->
            onFailure(e)
        }
}