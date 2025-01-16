package testMetrics.analyzer

data class ReadabilityMetrics(
    val usesBackticks: Boolean,
    val hasComments: Boolean,
    val averageTestLength: Int,
    override val score: Double = calculateScore(usesBackticks, hasComments, averageTestLength)
) : Metrics {
    companion object {
        private fun calculateScore(
            usesBackticks: Boolean,
            hasComments: Boolean,
            averageTestLength: Int
        ): Double {
            var score = 0.0

            // Бали за використання бектіків (30%)
            if (usesBackticks) score += 30.0

            // Бали за наявність коментарів (30%)
            if (hasComments) score += 30.0

            // Бали за оптимальну довжину тестів (40%)
            // Оптимальною вважаємо довжину від 5 до 25 рядків
            score += when {
                averageTestLength == 0 -> 0.0
                averageTestLength in 5..25 -> 40.0
                averageTestLength < 5 -> (averageTestLength / 5.0) * 40.0
                else -> (25.0 / averageTestLength) * 40.0
            }

            return score
        }
    }
}