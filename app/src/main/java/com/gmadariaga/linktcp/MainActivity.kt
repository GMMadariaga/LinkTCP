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
import androidx.compose.ui.res.stringResource
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

    val labels = mapOf(
        AppDestinations.CONNECTION to stringResource(R.string.nav_connect),
        AppDestinations.LOG to stringResource(R.string.nav_log),
        AppDestinations.ABOUT to stringResource(R.string.nav_about)
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                val label = labels[destination] ?: ""
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = label
                        )
                    },
                    label = { Text(label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
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
    val icon: ImageVector,
) {
    CONNECTION(Icons.Default.Cable),
    LOG(Icons.Default.History),
    ABOUT(Icons.Default.Info),
}
