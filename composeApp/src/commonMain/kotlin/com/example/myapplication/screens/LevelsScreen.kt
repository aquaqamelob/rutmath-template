package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Screen
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.back
import myapplication.composeapp.generated.resources.fragment_modes_add_sub
import myapplication.composeapp.generated.resources.fragment_modes_divisibility
import myapplication.composeapp.generated.resources.fragment_modes_mul_div
import myapplication.composeapp.generated.resources.fragment_modes_table
import myapplication.composeapp.generated.resources.fragment_modes_units
import org.jetbrains.compose.resources.stringResource

@Composable
fun LevelsScreen(
    navController: NavController
) {
    val backText = stringResource(Res.string.back)

    val maxUnlockedIdState = remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeContentPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Levels",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LevelSystem.levels) { level ->
                val isUnlocked = level.id <= maxUnlockedIdState.value
                LevelRow(
                    level = level,
                    isUnlocked = isUnlocked,
                    onClick = {
                        if (!isUnlocked) return@LevelRow

                        // Simple mapping from GameMode to existing routes.
                        val targetRoute = when (level.gameMode) {
                            GameMode.ADD_SUBTRACT -> Screen.GameAddSubtract.route
                            GameMode.MULTIPLY_DIVIDE -> Screen.GameMultiplyDivide.route
                            GameMode.DIVISIBILITY -> Screen.GameDivisibility.route
                            GameMode.UNIT_CONVERSION -> Screen.GameUnitConversion.route
                            GameMode.MULTIPLICATION_TABLE -> Screen.GameMultiplicationTable.route
                        }

                        navController.navigate(targetRoute)
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = backText,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LevelRow(
    level: Level,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val modeLabel = when (level.gameMode) {
        GameMode.ADD_SUBTRACT -> stringResource(Res.string.fragment_modes_add_sub)
        GameMode.MULTIPLY_DIVIDE -> stringResource(Res.string.fragment_modes_mul_div)
        GameMode.DIVISIBILITY -> stringResource(Res.string.fragment_modes_divisibility)
        GameMode.UNIT_CONVERSION -> stringResource(Res.string.fragment_modes_units)
        GameMode.MULTIPLICATION_TABLE -> stringResource(Res.string.fragment_modes_table)
    }

    val background = if (isUnlocked) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isUnlocked) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = background,
            contentColor = contentColor,
            disabledContainerColor = background,
            disabledContentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Level ${level.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = modeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}

