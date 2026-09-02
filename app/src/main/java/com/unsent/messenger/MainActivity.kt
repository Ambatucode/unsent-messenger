package com.unsent.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unsent.messenger.ui.navigation.Screen
import com.unsent.messenger.ui.screens.AiAssistantScreen
import com.unsent.messenger.ui.screens.ChatDetailScreen
import com.unsent.messenger.ui.screens.ConversationListScreen
import com.unsent.messenger.ui.screens.PermissionScreen
import com.unsent.messenger.ui.screens.SettingsScreen
import com.unsent.messenger.ui.theme.MessengerUnsentViewerTheme
import com.unsent.messenger.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessengerUnsentViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermissions(this)
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkPermissions(context)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Conversations.route
    ) {
        composable(Screen.Conversations.route) {
            ConversationListScreen(
                viewModel = viewModel,
                onNavigateToChat = { convId, title ->
                    navController.navigate(Screen.ChatDetail.createRoute(convId, title))
                },
                onNavigateToPermissions = {
                    navController.navigate(Screen.Permissions.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToAi = {
                    navController.navigate(Screen.AiAssistant.route)
                }
            )
        }

        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rawConvId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val rawTitle = backStackEntry.arguments?.getString("title") ?: ""
            val convId = Screen.ChatDetail.decodeParam(rawConvId)
            val title = Screen.ChatDetail.decodeParam(rawTitle)

            ChatDetailScreen(
                conversationId = convId,
                title = title,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionScreen(
                isNotificationAccessGranted = viewModel.isNotificationAccessGranted.value,
                isBatteryOptimizationIgnored = viewModel.isBatteryOptimizationIgnored.value,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToPermissions = {
                    navController.navigate(Screen.Permissions.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AiAssistant.route) {
            AiAssistantScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
