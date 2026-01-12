package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.cash.sqldelight.db.SqlDriver
import com.example.myapplication.database.ensureTablesExist
import com.example.myapplication.locale.AppLocaleProvider
import com.example.myapplication.locale.customAppLocale
import com.example.myapplication.screens.MainScreen
import com.example.myapplication.screens.GameMode
import com.example.myapplication.screens.GameScreen
import com.example.myapplication.screens.PlayerSelectionScreen
import com.example.myapplication.screens.PvPBattleScreen
import com.example.myapplication.screens.SelectionScreen
import com.example.myapplication.screens.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview
import myapplication.composeapp.generated.resources.Res

import myapplication.composeapp.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.DrawableResource

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Details : Screen("details")
    object Selection : Screen("selection")
    object PlayerSelection : Screen("player-selection")
    object PvP : Screen("pvp")
    object Leaderboard : Screen("leaderboard")
    object Settings : Screen("settings")
    // New game mode routes
    object GameAddSubtract : Screen("game-add-subtract")
    object GameMultiplyDivide : Screen("game-multiply-divide")
    object GameDivisibility : Screen("game-divisibility")
    object GameUnitConversion : Screen("game-unit-conversion")
    object GameMultiplicationTable : Screen("game-multiplication-table")
}

// Global state to hold the selected player ID
var selectedPlayerId by mutableStateOf<Long?>(null)

@Composable
@Preview
fun App(driver: SqlDriver) {
    val navController = rememberNavController()

    // Ensure all tables exist (handles migration issues)
    LaunchedEffect(Unit) {
//        ensureTablesExist(driver)
    }

    val db = Database(driver)
    
    // Load language preference from database
    LaunchedEffect(Unit) {
        try {
            val savedLanguage = db.databaseQueries.getLanguage().executeAsOneOrNull()
            if (savedLanguage != null) {
                customAppLocale = savedLanguage
            }
        } catch (e: Exception) {
            // Database might not have the table yet
        }
    }

    MaterialTheme {
        AppLocaleProvider {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) {
                    MainScreen(navController = navController)
                }

                composable(Screen.Settings.route) {
                    Settings(navController = navController, database = db)
                }
                
                composable(Screen.PlayerSelection.route) {
                    PlayerSelectionScreen(
                        navController = navController, 
                        database = db,
                        onPlayerSelected = { player ->
                            // Store selected player and navigate to game selection
                            selectedPlayerId = player.id
                            navController.navigate(Screen.Selection.route)
                        }
                    )
                }
                
                composable(Screen.PvP.route) {
                    PvPBattleScreen(
                        navController = navController,
                        player1Name = "Player 1",
                        player2Name = "Player 2"
                    )
                }

                // Game Mode: Addition & Subtraction
                composable(Screen.GameAddSubtract.route) {
                    GameScreen(
                        navController = navController,
                        gameMode = GameMode.ADD_SUBTRACT,
                        database = db,
                        playerId = selectedPlayerId
                    )
                }

                // Game Mode: Multiplication & Division
                composable(Screen.GameMultiplyDivide.route) {
                    GameScreen(
                        navController = navController,
                        gameMode = GameMode.MULTIPLY_DIVIDE,
                        database = db,
                        playerId = selectedPlayerId
                    )
                }

                // Game Mode: Divisibility
                composable(Screen.GameDivisibility.route) {
                    GameScreen(
                        navController = navController,
                        gameMode = GameMode.DIVISIBILITY,
                        database = db,
                        playerId = selectedPlayerId
                    )
                }

                // Game Mode: Unit Conversion
                composable(Screen.GameUnitConversion.route) {
                    GameScreen(
                        navController = navController,
                        gameMode = GameMode.UNIT_CONVERSION,
                        database = db,
                        playerId = selectedPlayerId
                    )
                }

                // Game Mode: Multiplication Table
                composable(Screen.GameMultiplicationTable.route) {
                    GameScreen(
                        navController = navController,
                        gameMode = GameMode.MULTIPLICATION_TABLE,
                        database = db,
                        playerId = selectedPlayerId
                    )
                }

                composable(Screen.Selection.route) {
                    SelectionScreen(navController = navController)
                }
            }
        }
    }
}
