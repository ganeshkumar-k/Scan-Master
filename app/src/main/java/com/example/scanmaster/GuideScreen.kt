package com.example.scanmaster

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hololo.tutorial.library.Step
import com.hololo.tutorial.library.TutorialActivity
import com.example.scanmaster.ui.theme.ScanMasterTheme

class GuideScreen : TutorialActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add tutorial steps
        addFragment(
            Step.Builder()
                .setTitle("Text Recognition from Images Using ML")
                .setContent("Recognize text in images with ML Kit on Android")
                .setBackgroundColor(Color.parseColor("#1A237E"))
                .setDrawable(R.drawable.scan_document)
                .setSummary("I am using ML Kit to recognize text in images or video, such as the text of a street sign.")
                .build()
        )

        addFragment(
            Step.Builder()
                .setTitle("Easily copy and utilize extracted text")
                .setContent("Recognize text in images with ML Kit on Android")
                .setBackgroundColor(Color.parseColor("#1E3A5F"))
                .setDrawable(R.drawable.copy_text)
                .setSummary("I am using ML Kit to recognize text in images or video, such as the text of a street sign.")
                .build()
        )

        addFragment(
            Step.Builder()
                .setTitle("This is header")
                .setContent("Recognize text in images with ML Kit on Android")
                .setBackgroundColor(Color.parseColor("#264653"))
                .setDrawable(R.drawable.scan_document)
                .setSummary("I am using ML Kit to recognize text in images or video, such as the text of a street sign.")
                .build()
        )

        // Use Jetpack Compose to handle the 'Finish Tutorial' button
        setContent {
            ScanMasterTheme {
                FinishTutorialButton {
                    navigateToMainActivity()
                }
            }
        }
    }

    override fun currentFragmentPosition(position: Int) {
        // Basic implementation to log current position
        Log.d("GuideScreen", "Current fragment position: $position")
    }



    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Composable
    fun FinishTutorialButton(onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onClick) {
                Text(text = "Finish Tutorial", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Preview
    @Composable
    fun PreviewFinishTutorialButton() {
        ScanMasterTheme {
            FinishTutorialButton {}
        }
    }
}
