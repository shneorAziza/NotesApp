package com.shneor.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.shneor.notesapp.repository.AuthRepository
import com.shneor.notesapp.ui.theme.NotesAppTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shneor.notesapp.screens.AuthScreen
import com.shneor.notesapp.screens.MainScreen
import com.shneor.notesapp.screens.NoteScreen
import com.shneor.notesapp.viewmodel.AuthViewModel
import com.shneor.notesapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.firestore.FirebaseFirestore

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            Navigation()
        }
    }
}

@Composable
fun Navigation() {

    val navController = rememberNavController()

    val mainViewModel: MainViewModel = hiltViewModel()

    val auth = FirebaseAuth.getInstance()

    val firestore = FirebaseFirestore.getInstance()

    val authRepository = AuthRepository(auth, firestore)

    val startDestination = if (authRepository.getCurrentUser() != null) "main" else "auth"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("auth") {
            AuthScreen(navController = navController, viewModel = AuthViewModel(authRepository))
        }
        composable("main") {
            MainScreen(navController = navController, viewModel = mainViewModel)
        }
        composable(
            route = "note_screen/{noteId}?latitude={latitude}&longitude={longitude}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("latitude") {
                    type = NavType.FloatType
                    defaultValue = 0f
                },
                navArgument("longitude") {
                    type = NavType.FloatType
                    defaultValue = 0f
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            val latitude = backStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 0.0
            val longitude = backStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: 0.0
            NoteScreen(
                navController = navController,
                noteId = noteId,
                initialLatitude = latitude,
                initialLongitude = longitude,
                onDeleteNote = { id ->
                    mainViewModel.deleteNote(id)
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesAppTheme {
        Greeting("Android")
    }
}