package com.example.roomjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.roomjetpackcompose.data.NotesDatabase
import com.example.roomjetpackcompose.presentation.AddNoteScreen
import com.example.roomjetpackcompose.presentation.NotesScreen
import com.example.roomjetpackcompose.presentation.NotesViewModel
import com.example.roomjetpackcompose.ui.theme.RoomJetpackComposeTheme

class MainActivity : ComponentActivity(){

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "notes.db"
        ).build()
    }
    private val viewModel by viewModels<NotesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotesViewModel(database.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "NoteScreen"){
                        composable("NoteScreen"){
                            NotesScreen(
                                state = state,
                                navController = navController,
                                onEvent = viewModel::onEvent
                                /*
                                onEvent = { event ->
                                    viewModel.onEvent(event)
                                }
                                 */
                            )
                        }
                        composable("AddNoteScreen"){
                            AddNoteScreen(
                                state = state,
                                navController = navController,
                                onEvent = viewModel::onEvent
                            )
                        }
                    }
                }
            }
        }
    }
}
