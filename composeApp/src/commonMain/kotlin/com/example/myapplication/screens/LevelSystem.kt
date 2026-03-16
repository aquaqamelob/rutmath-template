package com.example.myapplication.screens

data class Level(
    val id: Int,
    val title: String,
    val gameMode: GameMode,
    val difficulty: Difficulty,
    val totalQuestions: Int,
    val requiredCorrect: Int
)

enum class Difficulty { EASY, MEDIUM, HARD }
object LevelSystem {

    val levels: List<Level> = listOf(
        Level(
            id = 1,
            title = "1",
            gameMode = GameMode.ADD_SUBTRACT,
            difficulty = Difficulty.EASY,
            totalQuestions = 8,
            requiredCorrect = 5
        ),
        Level(
            id = 2,
            title = "2",
            gameMode = GameMode.ADD_SUBTRACT,
            difficulty = Difficulty.MEDIUM,
            totalQuestions = 10,
            requiredCorrect = 7
        ),
        Level(
            id = 3,
            title = "3",
            gameMode = GameMode.ADD_SUBTRACT,
            difficulty = Difficulty.HARD,
            totalQuestions = 12,
            requiredCorrect = 10
        ),

        Level(
            id = 4,
            title = "4",
            gameMode = GameMode.MULTIPLY_DIVIDE,
            difficulty = Difficulty.EASY,
            totalQuestions = 8,
            requiredCorrect = 5
        ),
        Level(
            id = 5,
            title = "5",
            gameMode = GameMode.MULTIPLY_DIVIDE,
            difficulty = Difficulty.MEDIUM,
            totalQuestions = 10,
            requiredCorrect = 7
        ),
        Level(
            id = 6,
            title = "6",
            gameMode = GameMode.MULTIPLY_DIVIDE,
            difficulty = Difficulty.HARD,
            totalQuestions = 12,
            requiredCorrect = 10
        ),

        Level(
            id = 7,
            title = "7",
            gameMode = GameMode.DIVISIBILITY,
            difficulty = Difficulty.EASY,
            totalQuestions = 8,
            requiredCorrect = 5
        ),
        Level(
            id = 8,
            title = "8",
            gameMode = GameMode.DIVISIBILITY,
            difficulty = Difficulty.MEDIUM,
            totalQuestions = 10,
            requiredCorrect = 7
        ),
        Level(
            id = 9,
            title = "9",
            gameMode = GameMode.DIVISIBILITY,
            difficulty = Difficulty.HARD,
            totalQuestions = 12,
            requiredCorrect = 10
        ),

        Level(
            id = 10,
            title = "10",
            gameMode = GameMode.UNIT_CONVERSION,
            difficulty = Difficulty.EASY,
            totalQuestions = 8,
            requiredCorrect = 5
        ),
        Level(
            id = 11,
            title = "11",
            gameMode = GameMode.UNIT_CONVERSION,
            difficulty = Difficulty.MEDIUM,
            totalQuestions = 10,
            requiredCorrect = 7
        ),
        Level(
            id = 12,
            title = "12",
            gameMode = GameMode.UNIT_CONVERSION,
            difficulty = Difficulty.HARD,
            totalQuestions = 12,
            requiredCorrect = 10
        ),

        Level(
            id = 13,
            title = "13",
            gameMode = GameMode.MULTIPLICATION_TABLE,
            difficulty = Difficulty.EASY,
            totalQuestions = 8,
            requiredCorrect = 5
        ),
        Level(
            id = 14,
            title = "14",
            gameMode = GameMode.MULTIPLICATION_TABLE,
            difficulty = Difficulty.MEDIUM,
            totalQuestions = 10,
            requiredCorrect = 7
        ),
        Level(
            id = 15,
            title = "15",
            gameMode = GameMode.MULTIPLICATION_TABLE,
            difficulty = Difficulty.HARD,
            totalQuestions = 12,
            requiredCorrect = 10
        )
    )

    fun firstLevelForMode(mode: GameMode): Level? =
        levels.firstOrNull { it.gameMode == mode }

    fun nextLevel(current: Level): Level? =
        levels.dropWhile { it.id != current.id }.drop(1).firstOrNull()

    fun isPassed(level: Level, correctAnswers: Int): Boolean =
        correctAnswers >= level.requiredCorrect
}

