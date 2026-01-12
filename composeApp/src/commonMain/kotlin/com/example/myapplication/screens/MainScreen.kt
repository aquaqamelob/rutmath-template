package com.example.myapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.Screen
import com.example.myapplication.components.MenuButton
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.bg_image
import myapplication.composeapp.generated.resources.ic_dumbbell_solid
import myapplication.composeapp.generated.resources.ic_gamepad_solid
import myapplication.composeapp.generated.resources.ic_settings_24dp
import myapplication.composeapp.generated.resources.ic_trophy_solid
import myapplication.composeapp.generated.resources.logo
import myapplication.composeapp.generated.resources.prz_logo
import myapplication.composeapp.generated.resources.weii_logo
import myapplication.composeapp.generated.resources.menu_play
import myapplication.composeapp.generated.resources.menu_settings
import myapplication.composeapp.generated.resources.menu_pvp
import myapplication.composeapp.generated.resources.menu_leaderboard
import myapplication.composeapp.generated.resources.fragment_menu_university
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    navController: NavController
) {
    val universityText = stringResource(Res.string.fragment_menu_university)
    val playText = stringResource(Res.string.menu_play)
    val pvpText = stringResource(Res.string.menu_pvp)
    val settingsText = stringResource(Res.string.menu_settings)
    val leaderboardText = stringResource(Res.string.menu_leaderboard)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.bg_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding() // respect iOS/Android safe areas (status bar / notch / home indicator)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top Logos (PRZ + WEii)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(Res.drawable.prz_logo),
                    contentDescription = "PRZ logo",
                    modifier = Modifier
                        .size(width = 150.dp, height = 60.dp)
                )

                Image(
                    painter = painterResource(Res.drawable.weii_logo),
                    contentDescription = "WEii logo",
                    modifier = Modifier
                        .size(width = 150.dp, height = 60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logo + Title Section
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "App logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "RUT Math",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = universityText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            MenuButton(iconRes = Res.drawable.ic_dumbbell_solid, text = playText) {
                navController.navigate(route = Screen.PlayerSelection.route)
            }

            MenuButton(iconRes = Res.drawable.ic_gamepad_solid, text = pvpText) {
                navController.navigate(route = Screen.PvP.route)
            }

            MenuButton(iconRes = Res.drawable.ic_trophy_solid, text = leaderboardText) {
                navController.navigate(route = Screen.PlayerSelection.route)
            }

            MenuButton(iconRes = Res.drawable.ic_settings_24dp, text = settingsText) {
                navController.navigate(route = Screen.Settings.route)
            }
        }
    }
}