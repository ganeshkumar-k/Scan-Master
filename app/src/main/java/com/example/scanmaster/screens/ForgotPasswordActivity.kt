package com.example.scanmaster.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPasswordActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreen(
                onResetPassword = { email -> resetPassword(email) },
                onBackToLogin = { finish() }
            )
        }
    }

    private fun resetPassword(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(this) { resetTask ->
                            if (resetTask.isSuccessful) {
                                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this, "Error: ${resetTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Invalid email. Please check and try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun ForgotPasswordScreen(
    onResetPassword: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE7F6))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .animateContentSize(animationSpec = tween(700)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(initialOffsetY = { -100 }) + fadeIn(tween(800))
            ) {
                Text(
                    text = "Reset Your Password",
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF512DA8)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(initialOffsetY = { -50 }) + fadeIn(tween(1000))
            ) {
                Text(
                    text = "Enter your email to reset your password",
                    style = TextStyle(fontSize = 20.sp, color = Color.Gray),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = expandVertically(animationSpec = tween(800)) + fadeIn()
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = scaleIn(tween(700)) + fadeIn()
            ) {
                Button(
                    onClick = { onResetPassword(email) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Send Reset Email",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(1000))
            ) {
                TextButton(onClick = onBackToLogin) {
                    Text(
                        text = "Back to Login",
                        style = TextStyle(color = Color(0xFF512DA8), fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}