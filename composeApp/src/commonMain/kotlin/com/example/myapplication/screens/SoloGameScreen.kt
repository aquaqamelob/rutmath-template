package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.Database
import kotlinx.coroutines.launch
import kotlin.random.Random

// Game modes enum
enum class GameMode(val title: String) {
    ADD_SUBTRACT("Addition & Subtraction"),
    MULTIPLY_DIVIDE("Multiplication & Division"),
    DIVISIBILITY("Divisibility"),
    UNIT_CONVERSION("Unit Conversion"),
    MULTIPLICATION_TABLE("Multiplication Table")
}

// Question data class that supports different answer types
data class GameQuestion(
    val questionText: String,
    val correctAnswer: String,
    val answerType: AnswerType = AnswerType.NUMBER
)

enum class AnswerType {
    NUMBER,
    YES_NO
}

// Legacy enum for backwards compatibility
enum class MathOperator(
    val symbol: String,
    val title: String
) {
    ADD("+", "Addition"),
    SUBTRACT("-", "Subtraction"),
    MULTIPLY("×", "Multiplication"),
    DIVIDE("÷", "Division")
}

fun generateMathQuestion(operator: MathOperator): Pair<String, Int> {
    val a = (1..10).random()
    val b = (1..10).random()

    return when (operator) {
        MathOperator.ADD -> "$a + $b = ?" to (a + b)
        MathOperator.SUBTRACT -> "$a - $b = ?" to (a - b)
        MathOperator.MULTIPLY -> "$a × $b = ?" to (a * b)
        MathOperator.DIVIDE -> {
            val result = a * b
            "$result ÷ $a = ?" to b
        }
    }
}

// New question generators for each game mode
fun generateAddSubtractQuestion(): GameQuestion {
    val a = (1..50).random()
    val b = (1..50).random()
    return if (listOf(true, false).random()) {
        GameQuestion("$a + $b = ?", (a + b).toString())
    } else {
        val larger = maxOf(a, b)
        val smaller = minOf(a, b)
        GameQuestion("$larger - $smaller = ?", (larger - smaller).toString())
    }
}

fun generateMultiplyDivideQuestion(): GameQuestion {
    val a = (1..12).random()
    val b = (1..12).random()
    return if (listOf(true, false).random()) {
        GameQuestion("$a × $b = ?", (a * b).toString())
    } else {
        val product = a * b
        GameQuestion("$product ÷ $a = ?", b.toString())
    }
}

fun generateDivisibilityQuestion(): GameQuestion {

    val divisor = (2..10).random()

    val multiplier = (2..20).random()


    val isDivisible = Random.nextBoolean()


    val number = (divisor * multiplier) + if (isDivisible) 0 else 1

    return GameQuestion(
        "Is $number divisible by $divisor?",
        if (isDivisible) "YES" else "NO",
        AnswerType.YES_NO
    )
}

fun generateUnitConversionQuestion(): GameQuestion {
    val conversions = listOf(
        Triple("hours", "minutes", 60),
        Triple("minutes", "seconds", 60),
        Triple("meters", "centimeters", 100),
        Triple("kilometers", "meters", 1000),
        Triple("kilograms", "grams", 1000),
        Triple("days", "hours", 24)
    )
    
    val (fromUnit, toUnit, factor) = conversions.random()
    val value = (1..10).random()
    
    return GameQuestion(
        "$value $fromUnit = ? $toUnit",
        (value * factor).toString()
    )
}

fun generateMultiplicationTableQuestion(): GameQuestion {
    val a = (1..12).random()
    val b = (1..12).random()
    return GameQuestion("$a × $b = ?", (a * b).toString())
}

fun generateQuestionForMode(mode: GameMode): GameQuestion {
    return when (mode) {
        GameMode.ADD_SUBTRACT -> generateAddSubtractQuestion()
        GameMode.MULTIPLY_DIVIDE -> generateMultiplyDivideQuestion()
        GameMode.DIVISIBILITY -> generateDivisibilityQuestion()
        GameMode.UNIT_CONVERSION -> generateUnitConversionQuestion()
        GameMode.MULTIPLICATION_TABLE -> generateMultiplicationTableQuestion()
    }
}

@Composable
fun SoloGameScreen(
    navController: NavController,
    operator: MathOperator,
    generateQuestion: (MathOperator) -> Pair<String, Int>,
    database: Database? = null,
    playerId: Long? = null
) {
    // Convert old operator to new game mode
    val gameMode = when (operator) {
        MathOperator.ADD, MathOperator.SUBTRACT -> GameMode.ADD_SUBTRACT
        MathOperator.MULTIPLY, MathOperator.DIVIDE -> GameMode.MULTIPLY_DIVIDE
    }
    
    GameScreen(
        navController = navController,
        gameMode = gameMode,
        database = database,
        playerId = playerId
    )
}

@Composable
fun GameScreen(
    navController: NavController,
    gameMode: GameMode,
    database: Database? = null,
    playerId: Long? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val totalQuestions = 10

    var currentQuestionData by remember { mutableStateOf<GameQuestion?>(null) }
    var userAnswer by remember { mutableStateOf("") }

    var currentQuestion by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var scoreSaved by remember { mutableStateOf(false) }
    
    var playerName by remember { mutableStateOf<String?>(null) }
    
    // Load player name
    LaunchedEffect(playerId) {
        if (playerId != null && database != null) {
            try {
                val player = database.databaseQueries.getPlayerById(playerId).executeAsOneOrNull()
                playerName = player?.nickname
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun newQuestion() {
        currentQuestionData = generateQuestionForMode(gameMode)
        userAnswer = ""
    }
    
    // Save score when game is over
    fun saveScore() {
        if (!scoreSaved && playerId != null && database != null) {
            coroutineScope.launch {
                try {
                    val player = database.databaseQueries.getPlayerById(playerId).executeAsOneOrNull()
                    if (player != null) {
                        val newHighScore = player.high_score + score.toLong()
                        database.databaseQueries.updatePlayerScore(newHighScore, playerId)
                        scoreSaved = true
                    }
                } catch (e: Exception) {
                    // Ignore errors
                }
            }
        }
    }

    // First question
    if (currentQuestionData == null) {
        newQuestion()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .safeContentPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header section
        if (playerName != null) {
            Text(
                text = playerName!!,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = gameMode.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { currentQuestion / totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )

        Spacer(Modifier.height(8.dp))

        if (gameOver) {
            LaunchedEffect(gameOver) {
                saveScore()
            }
            
            GameOverContent(
                score = score,
                totalQuestions = totalQuestions,
                playerName = playerName,
                scoreSaved = scoreSaved,
                onPlayAgain = {
                    currentQuestion = 1
                    score = 0
                    gameOver = false
                    scoreSaved = false
                    newQuestion()
                },
                onBack = { navController.popBackStack() }
            )
            return@Column
        }

        // Question display
        Text(
            text = "Question $currentQuestion / $totalQuestions",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(Modifier.height(16.dp))

        // Question text - larger display
        Text(
            text = currentQuestionData?.questionText ?: "",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Answer display
        Text(
            text = if (userAnswer.isEmpty()) "?" else userAnswer,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.weight(1f))

        // Keyboard section - takes remaining space
        if (currentQuestionData?.answerType == AnswerType.YES_NO) {
            YesNoButtons(
                onYes = {
                    userAnswer = "YES"
                    if (currentQuestionData?.correctAnswer == "YES") score++
                    if (currentQuestion == totalQuestions) {
                        gameOver = true
                    } else {
                        currentQuestion++
                        newQuestion()
                    }
                },
                onNo = {
                    userAnswer = "NO"
                    if (currentQuestionData?.correctAnswer == "NO") score++
                    if (currentQuestion == totalQuestions) {
                        gameOver = true
                    } else {
                        currentQuestion++
                        newQuestion()
                    }
                }
            )
        } else {
            FullScreenNumberPad(
                onNumberClick = { userAnswer += it },
                onDelete = { if (userAnswer.isNotEmpty()) userAnswer = userAnswer.dropLast(1) },
                onClear = { userAnswer = "" },
                onSubmit = {
                    if (userAnswer.isNotEmpty()) {
                        if (userAnswer == currentQuestionData?.correctAnswer) {
                            score++
                        }
                        if (currentQuestion == totalQuestions) {
                            gameOver = true
                        } else {
                            currentQuestion++
                            newQuestion()
                        }
                    }
                },
                canSubmit = userAnswer.isNotEmpty()
            )
        }
        
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun GameOverContent(
    score: Int,
    totalQuestions: Int,
    playerName: String?,
    scoreSaved: Boolean,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉 Game Over! 🎉",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "$score / $totalQuestions",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(Modifier.height(8.dp))
        
        val percentage = (score.toFloat() / totalQuestions * 100).toInt()
        Text(
            text = when {
                percentage >= 90 -> "Excellent! 🌟"
                percentage >= 70 -> "Great job! 👍"
                percentage >= 50 -> "Good effort! 💪"
                else -> "Keep practicing! 📚"
            },
            style = MaterialTheme.typography.titleLarge
        )
        
        if (scoreSaved && playerName != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Score saved for $playerName!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp)
        ) {
            Text("Play Again", fontSize = 18.sp)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Back", fontSize = 18.sp)
        }
    }
}

@Composable
fun YesNoButtons(
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onYes,
            modifier = Modifier
                .weight(1f)
                .height(120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "YES",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Button(
            onClick = onNo,
            modifier = Modifier
                .weight(1f)
                .height(120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "NO",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FullScreenNumberPad(
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    canSubmit: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: 1 2 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FullWidthKeyButton("1", Modifier.weight(1f)) { onNumberClick("1") }
            FullWidthKeyButton("2", Modifier.weight(1f)) { onNumberClick("2") }
            FullWidthKeyButton("3", Modifier.weight(1f)) { onNumberClick("3") }
        }
        
        // Row 2: 4 5 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FullWidthKeyButton("4", Modifier.weight(1f)) { onNumberClick("4") }
            FullWidthKeyButton("5", Modifier.weight(1f)) { onNumberClick("5") }
            FullWidthKeyButton("6", Modifier.weight(1f)) { onNumberClick("6") }
        }
        
        // Row 3: 7 8 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FullWidthKeyButton("7", Modifier.weight(1f)) { onNumberClick("7") }
            FullWidthKeyButton("8", Modifier.weight(1f)) { onNumberClick("8") }
            FullWidthKeyButton("9", Modifier.weight(1f)) { onNumberClick("9") }
        }
        
        // Row 4: Clear 0 Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FullWidthKeyButton("C", Modifier.weight(1f), Color(0xFFFF9800)) { onClear() }
            FullWidthKeyButton("0", Modifier.weight(1f)) { onNumberClick("0") }
            FullWidthKeyButton("⌫", Modifier.weight(1f), Color(0xFFF44336)) { onDelete() }
        }
        
        // Row 5: Minus and Submit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FullWidthKeyButton("-", Modifier.weight(1f)) { onNumberClick("-") }
            Button(
                onClick = onSubmit,
                enabled = canSubmit,
                modifier = Modifier
                    .weight(2f)
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "CHECK ✓",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FullWidthKeyButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// Keep old NumberPad for backwards compatibility
@Composable
fun NumberPad(
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit
) {
    FullScreenNumberPad(
        onNumberClick = onNumberClick,
        onDelete = onDelete,
        onClear = onClear,
        onSubmit = {},
        canSubmit = false
    )
}
