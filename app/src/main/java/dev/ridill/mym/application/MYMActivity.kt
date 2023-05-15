package dev.ridill.mym.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.navigation.MYMNavHost
import dev.ridill.mym.core.ui.theme.MYMTheme
import dev.ridill.mym.core.util.isBuildAtLeastVersionCodeS

@AndroidEntryPoint
class MYMActivity : ComponentActivity() {

    private val viewModel: MymViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        setContent {
            val theme by viewModel.appTheme.collectAsStateWithLifecycle(AppTheme.SYSTEM_DEFAULT)
            val dynamicTheme by viewModel.materialYouTheme.collectAsStateWithLifecycle(isBuildAtLeastVersionCodeS())
            val darkTheme = when (theme) {
                AppTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }
            MYMTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicTheme
            ) {
                val navController = rememberNavController()
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    MYMNavHost(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}