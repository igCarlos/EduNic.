package com.crj4.edunic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.crj4.edunic.presentation.navigation.NavGraph
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.crj4.edunic.ui.theme.EduNicTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Instalar Splash correctamente
        val splashScreen = installSplashScreen()

        var keepSplash = true

        splashScreen.setKeepOnScreenCondition {
            keepSplash
        }

        Handler(Looper.getMainLooper()).postDelayed({
            keepSplash = false
        }, 2000)

        enableEdgeToEdge()

        setContent {
            EduNicTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                NavGraph(navController, authViewModel)
            }
        }
    }
}