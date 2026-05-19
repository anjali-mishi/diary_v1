package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.ui.AppNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    private val requestMicPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("Diary.MainActivity", "RECORD_AUDIO permission ${if (isGranted) "granted" else "denied"}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Diary.MainActivity", "onCreate: launching app (savedInstanceState=${savedInstanceState != null})")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(applicationContext).memoryDao()
            val referencedPaths = dao.getAllAudioFilePaths().toSet()
            val audioDir = File(filesDir, "audio_memos")
            if (audioDir.exists()) {
                audioDir.listFiles()?.forEach { file ->
                    if (file.absolutePath !in referencedPaths) {
                        Log.d("Diary.MainActivity", "Deleting orphaned audio: ${file.name}")
                        file.delete()
                    }
                }
            }
        }

        Log.d("Diary.MainActivity", "onCreate: content set")
    }

    override fun onStart() {
        super.onStart()
        Log.d("Diary.MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Diary.MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Diary.MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Diary.MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Diary.MainActivity", "onDestroy")
    }
}
