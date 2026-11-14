package com.endus.drivary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme
import com.endus.drivary.presentation.navigation.DrivaryNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrivaryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DrivaryNavHost(modifier = Modifier.safeDrawingPadding())
                }
            }
        }
    }
}
