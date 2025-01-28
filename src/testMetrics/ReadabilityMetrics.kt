package testMetrics

data class ReadabilityMetrics(
    val usesBackticks: Int = 0,  // Change to Int to count occurrences
    val hasComments: Int = 0,   // Change to Int to count occurrences
    val averageTestLength: Int = 0  // Keep as Int for accumulating lengths
)

fun ReadabilityMetrics.add(result: ReadabilityMetrics): ReadabilityMetrics { // Remove totalFiles parameter
    val newUsesBackticks = this.usesBackticks + (if (result.usesBackticks == 1) 1 else 0)
    val newHasComments = this.hasComments + (if (result.hasComments == 1) 1 else 0)
    val newAverageTestLength = this.averageTestLength + result.averageTestLength

    return ReadabilityMetrics(
        newUsesBackticks,
        newHasComments,
        newAverageTestLength
    )
}

fun ReadabilityMetrics.applyThreshold(totalFiles: Int): ReadabilityMetrics {
    return ReadabilityMetrics(
        if (this.usesBackticks / totalFiles.toDouble() >= 0.7) 1 else 0,
        if (this.hasComments / totalFiles.toDouble() >= 0.7) 1 else 0,
        this.averageTestLength / totalFiles // Calculate average length here
    )
}