package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.ui.AppNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Diary.MainActivity", "onCreate: launching app (savedInstanceState=${savedInstanceState != null})")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
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
