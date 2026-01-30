package com.gmadariaga.linktcp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gmadariaga.linktcp.presentation.screen.AboutScreen
import com.gmadariaga.linktcp.presentation.screen.HomeScreen
import com.gmadariaga.linktcp.presentation.screen.LogScreen
import com.gmadariaga.linktcp.presentation.viewmodel.ConnectionViewModel
import com.gmadariaga.linktcp.ui.theme.LinkTCPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkTCPTheme {
                LinkTCPApp()
            }
        }
    }
}

@Composable
fun LinkTCPApp() {
    val context = LocalContext.current
    val viewModel: ConnectionViewModel = viewModel(
        factory = ConnectionViewModel.Factory(context.applicationContext)
    )
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.CONNECTION) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.CONNECTION -> HomeScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.LOG -> LogScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.ABOUT -> AboutScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    CONNECTION("Connect", Icons.Default.Cable),
    LOG("Log", Icons.Default.History),
    ABOUT("About", Icons.Default.Info),
}
