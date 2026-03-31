@file:OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)

package com.example.myapplication.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween as colorTween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.repository.MemoryRepository
import com.example.myapplication.ui.viewmodel.CaptureViewModel
import com.example.myapplication.ui.viewmodel.DiaryViewModel

private const val TAG = "Diary.Navigation"

/** Provides the [SharedTransitionScope] to any composable in the nav graph. */
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

/** Provides the per-destination [AnimatedVisibilityScope] (`this` inside a composable lambda). */
val LocalNavAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Screens write `true` here as soon as their primary scroll container moves.
 * AppNavigation reads it to drive the scroll-reactive TopAppBar colour.
 * Reset to `false` automatically on every route change.
 */
val LocalTopBarScrolled = compositionLocalOf<MutableState<Boolean>> {
    mutableStateOf(false)
}

enum class Screen {
    Diary,
    Capture,
    Index,
    Detail
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    Log.d(TAG, "AppNavigation: composing")
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = remember { context.applicationContext as android.app.Application }

    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { MemoryRepository(db.memoryDao()) }

    val diaryViewModel: DiaryViewModel = viewModel(
        factory = DiaryViewModel.Factory(repository)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isIndex  = currentRoute?.startsWith(Screen.Index.name)  == true
    val isDetail = currentRoute?.startsWith(Screen.Detail.name) == true

    // Scroll-reactive TopAppBar: screens write true when scrolled, reset on route change
    val topBarScrolled = remember { mutableStateOf(false) }
    LaunchedEffect(currentRoute) { topBarScrolled.value = false }

    // Animate only the alpha channel so the interpolated colour stays warm
    // (lerping from Color.Transparent = black@0 produces dark brown intermediates)
    val topBarBg by animateColorAsState(
        targetValue = if (topBarScrolled.value)
            MaterialTheme.colorScheme.background.copy(alpha = 0.96f)
        else
            MaterialTheme.colorScheme.background.copy(alpha = 0f),
        animationSpec = colorTween(durationMillis = 200),
        label = "topBarBg"
    )

    val detailMemoryId = if (isDetail) navBackStackEntry?.arguments?.getString("memoryId") else null

    Scaffold(
        modifier = modifier,
        // TopAppBar lives here — the Scaffold guarantees it draws ABOVE all
        // NavHost content in every state, including during shared-element transitions.
        topBar = {
            if (isIndex || isDetail) {
                TopAppBar(
                    title = {
                        if (isIndex) {
                            Text(
                                text = "My Diaries",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        if (isDetail && detailMemoryId != null) {
                            IconButton(onClick = {
                                Log.d(TAG, "GlobalNav → Capture (edit memoryId=$detailMemoryId)")
                                navController.navigate("${Screen.Capture.name}?memoryId=$detailMemoryId")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit memory",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarBg)
                )
            }
        }
    ) { innerPadding ->
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
                LocalTopBarScrolled provides topBarScrolled
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Diary.name,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                        composable(
                            route = Screen.Diary.name,
                            // Screen appears immediately (0.99→1.0 is imperceptible) but the scope's
                            // transition stays active for 500ms so the sharedBounds spring can complete.
                            // Without this, popEnterTransition defaults to fadeIn(700ms) which makes all
                            // text and cards visibly fade in after closing a memory detail page.
                            popEnterTransition = { fadeIn(initialAlpha = 1f, animationSpec = tween(700)) }
                        ) {
                            Log.d(TAG, "Navigated to: Diary")
                            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
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
                                    onNavigateToDetail = { memoryId ->
                                        Log.d(TAG, "DiaryScreen → Detail (memoryId=$memoryId)")
                                        navController.navigate("${Screen.Detail.name}/$memoryId")
                                    },
                                    viewModel = diaryViewModel
                                )
                            }
                        }
                        composable(
                            route = "${Screen.Capture.name}?memoryId={memoryId}&action={action}",
                            arguments = listOf(
                                navArgument("memoryId") { type = NavType.StringType; nullable = true },
                                navArgument("action") { type = NavType.StringType; nullable = true }
                            ),
                            enterTransition = {
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(durationMillis = 400)
                                ) + fadeIn(animationSpec = tween(durationMillis = 300))
                            },
                            exitTransition = {
                                slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(durationMillis = 350)
                                ) + fadeOut(animationSpec = tween(durationMillis = 250))
                            }
                        ) { backStackEntry ->
                            val memoryId = backStackEntry.arguments?.getString("memoryId")
                            val action   = backStackEntry.arguments?.getString("action")
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
                        composable(
                            route = Screen.Index.name,
                            popEnterTransition = { fadeIn(initialAlpha = 1f, animationSpec = tween(700)) }
                        ) {
                            Log.d(TAG, "Navigated to: Index")
                            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                IndexScreen(
                                    onNavigateBack = {
                                        Log.d(TAG, "IndexScreen → back")
                                        navController.popBackStack()
                                    },
                                    onNavigateToEdit = { memoryId ->
                                        Log.d(TAG, "IndexScreen → Capture (edit memoryId=$memoryId)")
                                        navController.navigate("${Screen.Capture.name}?memoryId=$memoryId")
                                    },
                                    onNavigateToDetail = { memoryId ->
                                        Log.d(TAG, "IndexScreen → Detail (memoryId=$memoryId)")
                                        navController.navigate("${Screen.Detail.name}/$memoryId")
                                    },
                                    viewModel = diaryViewModel
                                )
                            }
                        }
                        composable(
                            route = "${Screen.Detail.name}/{memoryId}",
                            arguments = listOf(
                                navArgument("memoryId") { type = NavType.StringType }
                            ),
                            // Let sharedBounds drive the transition; suppress nav-level fades
                            enterTransition    = { EnterTransition.None },
                            exitTransition     = { ExitTransition.None },
                            popEnterTransition = { EnterTransition.None },
                            popExitTransition  = { ExitTransition.None }
                        ) { backStackEntry ->
                            val memoryId = backStackEntry.arguments?.getString("memoryId")
                                ?: return@composable
                            Log.d(TAG, "Navigated to: Detail (memoryId=$memoryId)")
                            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                MemoryDetailScreen(
                                    memoryId = memoryId,
                                    viewModel = diaryViewModel
                                )
                            }
                        }
                }
            }
        }
    }
}
