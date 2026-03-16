package com.example.myapplication.ui

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.repository.MemoryRepository
import com.example.myapplication.ui.viewmodel.CaptureViewModel
import com.example.myapplication.ui.viewmodel.DiaryViewModel

private const val TAG = "Diary.Navigation"

enum class Screen {
    Diary,
    Capture,
    Index
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    Log.d(TAG, "AppNavigation: composing")
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = remember { context.applicationContext as android.app.Application }

    // Set up database and repository once for the whole nav graph
    val db = com.example.myapplication.data.database.AppDatabase.getDatabase(context)
    val repository = com.example.myapplication.data.repository.MemoryRepository(db.memoryDao())

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
                Log.d(TAG, "Navigated to: Diary")
                DiaryScreen(
                    onNavigateToCapture = { action ->
                        Log.d(TAG, "DiaryScreen → Capture (action=$action)")
                        navController.navigate("${Screen.Capture.name}?action=$action")
                    },
                    onNavigateToIndex = {
                        Log.d(TAG, "DiaryScreen → Index")
                        navController.navigate(Screen.Index.name)
                    },
                    onNavigateToEdit = { memoryId ->
                        Log.d(TAG, "DiaryScreen → Capture (edit memoryId=$memoryId)")
                        navController.navigate("${Screen.Capture.name}?memoryId=$memoryId")
                    },
                    viewModel = diaryViewModel
                )
            }
            composable(
                route = "${Screen.Capture.name}?memoryId={memoryId}&action={action}",
                arguments = listOf(
                    navArgument("memoryId") { type = NavType.StringType; nullable = true },
                    navArgument("action") { type = NavType.StringType; nullable = true }
                ),
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 400)
                    ) + fadeIn(animationSpec = tween(durationMillis = 300))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 350)
                    ) + fadeOut(animationSpec = tween(durationMillis = 250))
                }
            ) { backStackEntry ->
                val memoryId = backStackEntry.arguments?.getString("memoryId")
                val action = backStackEntry.arguments?.getString("action")
                Log.d(TAG, "Navigated to: Capture (memoryId=$memoryId action=$action)")
                val captureViewModel: CaptureViewModel = viewModel(
                    factory = CaptureViewModel.Factory(application, repository)
                )
                CaptureScreen(
                    memoryId = memoryId,
                    action = action,
                    onNavigateBack = {
                        Log.d(TAG, "CaptureScreen → back")
                        navController.popBackStack()
                    },
                    viewModel = captureViewModel
                )
            }
            composable(route = Screen.Index.name) {
                Log.d(TAG, "Navigated to: Index")
                IndexScreen(
                    onNavigateBack = {
                        Log.d(TAG, "IndexScreen → back")
                        navController.popBackStack()
                    },
                    onNavigateToEdit = { memoryId ->
                        Log.d(TAG, "IndexScreen → Capture (edit memoryId=$memoryId)")
                        navController.navigate("${Screen.Capture.name}?memoryId=$memoryId")
                    },
                    viewModel = diaryViewModel
                )
            }
        }
    }
}
