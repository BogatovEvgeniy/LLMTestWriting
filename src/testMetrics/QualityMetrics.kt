package testMetrics

data class QualityMetrics(
    val hasDescriptiveNames: Int = 0,  // Change to Int to count occurrences
    val usesAssertionVariety: Int = 0, // Change to Int to count occurrences
    val hasTestDocumentation: Int = 0, // Change to Int to count occurrences
    val followsNamingConventions: Int = 0  // Change to Int to count occurrences
)

fun QualityMetrics.add(result: QualityMetrics): QualityMetrics { // Remove totalFiles parameter
    val newHasDescriptiveNames = this.hasDescriptiveNames + (if (result.hasDescriptiveNames == 1) 1 else 0)
    val newUsesAssertionVariety = this.usesAssertionVariety + (if (result.usesAssertionVariety == 1) 1 else 0)
    val newHasTestDocumentation = this.hasTestDocumentation + (if (result.hasTestDocumentation == 1) 1 else 0)
    val newFollowsNamingConventions = this.followsNamingConventions + (if (result.followsNamingConventions == 1) 1 else 0)

    return QualityMetrics(
        newHasDescriptiveNames, // Just accumulate the counts here
        newUsesAssertionVariety,
        newHasTestDocumentation,
        newFollowsNamingConventions
    )
}

fun QualityMetrics.applyThreshold(totalFiles: Int): QualityMetrics {
    return QualityMetrics(
        if (this.hasDescriptiveNames / totalFiles.toDouble() >= 0.7) 1 else 0,
        if (this.usesAssertionVariety / totalFiles.toDouble() >= 0.7) 1 else 0,
        if (this.hasTestDocumentation / totalFiles.toDouble() >= 0.7) 1 else 0,
        if (this.followsNamingConventions / totalFiles.toDouble() >= 0.7) 1 else 0
    )
}