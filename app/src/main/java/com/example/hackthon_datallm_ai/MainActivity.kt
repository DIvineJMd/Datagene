package com.example.hackthon_datallm_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hackthon_datallm_ai.Model.ViewModelChat
import com.example.hackthon_datallm_ai.Model.ViewModelChatFactory
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel
import com.example.hackthon_datallm_ai.ui.theme.ChatScreen
import com.example.hackthon_datallm_ai.ui.theme.Hackthon_DataLLM_AITheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Hackthon_DataLLM_AITheme {
                val navController = rememberNavController()
                val chatViewModel: ChatViewModel = viewModel()
                val viewModelChat: ViewModelChat = viewModel(
                    factory = ViewModelChatFactory(chatViewModel)
                )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Set up navigation graph
                    NavHost(
                        navController = navController,
                        startDestination = "main_screen"
                    ) {
                        composable("input") {
                            UIdatainput(applicationContext,navController).Databaseinput()
                        }
                        composable("main_screen"){
                            MainScreen().MainUi(context = applicationContext,navController,viewModelChat)
                        }
                        composable("chat"){
                            ChatScreen(navController = navController, viewmodel = viewModelChat)
                        }
                    }
                }
            }
        }
    }
}

