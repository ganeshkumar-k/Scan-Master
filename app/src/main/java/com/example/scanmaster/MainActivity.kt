package com.example.scanmaster

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scanmaster.ui.theme.ScanMasterTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.InputStream

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

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var detectedText by remember { mutableStateOf("Captured text content will be displayed here") }
    var isTextEmpty by remember { mutableStateOf(true) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val extras = result.data?.extras
            val bitmap = extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectTextUsingML(
                    bitmap,
                    onTextDetected = {
                        detectedText = it
                        isTextEmpty = it.isEmpty()
                    },
                    onFailure = { Toast.makeText(context, "Text detection failed", Toast.LENGTH_SHORT).show() }
                )
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(context, uri)
            if (bitmap != null) {
                detectTextUsingML(
                    bitmap,
                    onTextDetected = {
                        detectedText = it
                        isTextEmpty = it.isEmpty()
                    },
                    onFailure = { Toast.makeText(context, "Text detection failed", Toast.LENGTH_SHORT).show() }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ScanMaster", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomControls(
                onEraseClick = {
                    detectedText = "Captured text content will be displayed here"
                    isTextEmpty = true
                },
                onCopyClick = {
                    if (!isTextEmpty) {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Detected Text", detectedText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No text to copy", Toast.LENGTH_SHORT).show()
                    }
                },
                onCameraClick = {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(intent)
                },
                onGalleryClick = {
                    galleryLauncher.launch("image/*")
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
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
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Capture and Detect Text",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                CapturedTextCard(
                    text = detectedText,
                    isEmpty = isTextEmpty
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CapturedTextCard(text: String, isEmpty: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEmpty) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    color = if (isEmpty) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun BottomControls(
    onEraseClick: () -> Unit,
    onCopyClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(icon = Icons.Default.Delete, label = "Erase", color = MaterialTheme.colorScheme.error, onClick = onEraseClick)
        ControlButton(icon = Icons.Default.Add, label = "Camera", color = MaterialTheme.colorScheme.primary, onClick = onCameraClick)
        ControlButton(icon = Icons.Default.Home, label = "Gallery", color = MaterialTheme.colorScheme.secondary, onClick = onGalleryClick)
        ControlButton(icon = Icons.Default.Done, label = "Copy", color = MaterialTheme.colorScheme.tertiary, onClick = onCopyClick)
    }
}

@Composable
fun ControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier
                .size(50.dp)
                .background(color.copy(alpha = 0.2f), shape = CircleShape)
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color)
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

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}