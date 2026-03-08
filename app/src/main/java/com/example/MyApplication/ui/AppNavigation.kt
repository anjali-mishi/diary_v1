package com.example.myapplication.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.repository.MemoryRepository
import com.example.myapplication.ui.viewmodel.CaptureViewModel
import com.example.myapplication.ui.viewmodel.DiaryViewModel

enum class Screen {
    Diary,
    Capture,
    Index
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Set up database and repository once for the whole nav graph
    val db = AppDatabase.getDatabase(context)
    val repository = MemoryRepository(db.memoryDao())

    // DiaryViewModel shared between DiaryScreen and IndexScreen
    val diaryViewModel: DiaryViewModel = viewModel(
        factory = DiaryViewModel.Factory(repository)
    )

    Scaffold(modifier = modifier) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Diary.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Diary.name) {
                DiaryScreen(
                    onNavigateToCapture = { navController.navigate(Screen.Capture.name) },
                    onNavigateToIndex = { navController.navigate(Screen.Index.name) },
                    onNavigateToEdit = { memoryId -> navController.navigate("${Screen.Capture.name}?memoryId=$memoryId") },
                    viewModel = diaryViewModel
                )
            }
            composable(
                route = "${Screen.Capture.name}?memoryId={memoryId}",
                arguments = listOf(navArgument("memoryId") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val memoryId = backStackEntry.arguments?.getString("memoryId")
                val captureViewModel: CaptureViewModel = viewModel(
                    factory = CaptureViewModel.Factory(repository)
                )
                CaptureScreen(
                    memoryId = memoryId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = captureViewModel
                )
            }
            composable(route = Screen.Index.name) {
                IndexScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { memoryId -> navController.navigate("${Screen.Capture.name}?memoryId=$memoryId") },
                    viewModel = diaryViewModel
                )
            }
        }
    }
}
