package com.example.scanmaster.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanmaster.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                onLogin = { email, password -> loginUser(email, password) },
                onSignUp = { startActivity(Intent(this, SignUpActivity::class.java)) },
                onForgotPassword = { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
            )
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Email not verified. Please verify your email to log in.",
                            Toast.LENGTH_LONG
                        ).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

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
                    text = "Welcome Back!",
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
                    text = "Log in to continue",
                    style = TextStyle(fontSize = 20.sp, color = Color.Gray),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = expandVertically(animationSpec = tween(800)) + fadeIn()
            ) {
                Column {
                    TextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = ""
                        },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    if (emailError.isNotEmpty()) {
                        Text(
                            text = emailError,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = expandVertically(animationSpec = tween(900)) + fadeIn()
            ) {
                Column {
                    TextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = ""
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        trailingIcon = {
                            Text(
                                text = if (passwordVisible) "Hide" else "Show",
                                style = TextStyle(color = Color(0xFF512DA8)),
                                modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                            )
                        }
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = scaleIn(tween(700)) + fadeIn()
            ) {
                Button(
                    onClick = {
                        when {
                            email.isBlank() -> emailError = "Please enter your email."
                            password.isBlank() -> passwordError = "Please enter your password."
                            else -> onLogin(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Log In",
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
                TextButton(onClick = onForgotPassword) {
                    Text(
                        text = "Forgot Password?",
                        style = TextStyle(color = Color(0xFF512DA8), fontWeight = FontWeight.Medium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = slideInHorizontally(initialOffsetX = { -300 }) + fadeIn(tween(900))
            ) {
                TextButton(onClick = onSignUp) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        style = TextStyle(color = Color(0xFF512DA8), fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}