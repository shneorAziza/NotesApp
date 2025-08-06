package com.shneor.notesapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shneor.notesapp.viewmodel.AuthUiState
import com.shneor.notesapp.viewmodel.AuthViewModel

enum class AuthMode {
    LOGIN,
    SIGNUP
}

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }

    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }

    var passwordConfirm by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    val user by viewModel.user.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate("main") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    Box(
       Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (authMode == AuthMode.LOGIN) "Login" else "Sign Up",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = myFont
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                    text = "Email",
                    fontFamily = myFont
                ) }
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Password",
                        fontFamily = myFont
                    ) },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(8.dp))

            if (authMode == AuthMode.SIGNUP) {
                OutlinedTextField(
                    value = passwordConfirm,
                    onValueChange = { passwordConfirm = it },
                    label = {
                        Text(
                            text = "Confirm Password",
                            fontFamily = myFont
                        ) },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            text = "Name",
                            fontFamily = myFont
                        ) },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (authMode == AuthMode.LOGIN) {
                        viewModel.login(email, password)
                    } else {
                        if (password == passwordConfirm) {
                            viewModel.signup(email, password, name)
                        } else {
                            viewModel.setErrorMessage("Passwords do not match")
                        }
                    }
                },
                enabled = uiState !is AuthUiState.Loading
            ) {
                Text(
                    if (authMode == AuthMode.LOGIN) "Login" else "Sign Up",
                    fontFamily = myFont
                )
            }

            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator()
            }
            if (uiState is AuthUiState.Error) {
                val errorMessage = (uiState as AuthUiState.Error).message
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = myFont
                )
            }

            TextButton(onClick = {
                authMode = if (authMode == AuthMode.LOGIN) AuthMode.SIGNUP else AuthMode.LOGIN
                viewModel.resetUiState()
            }) {
                Text(
                    if (authMode == AuthMode.LOGIN) "Don't have an account? Sign up" else "Already have an account? Login",
                    fontFamily = myFont
                )
            }
        }
    }
}