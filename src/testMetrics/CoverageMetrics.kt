package testMetrics

data class CoverageMetrics(
    val methodsCovered: Int = 0,
    val edgeCasesCovered: List<EdgeCase>,
    val boundaryTests: List<String>,
    val edgeCasesCount: Int = 0,
    val boundaryTestsCount: Int = 0
)

fun CoverageMetrics.add(result: CoverageMetrics): CoverageMetrics {
    val edgeCaseCounts = mutableMapOf<String, Int>()

    // Count true cases from current
    this.edgeCasesCovered.forEach { edgeCase ->
        if (edgeCase.covered) {
            edgeCaseCounts[edgeCase.type] = edgeCaseCounts.getOrDefault(edgeCase.type, 0) + 1
        }
    }

    // Add counts from result
    result.edgeCasesCovered.forEach { edgeCase ->
        if (edgeCase.covered) {
            edgeCaseCounts[edgeCase.type] = edgeCaseCounts.getOrDefault(edgeCase.type, 0) + 1
        }
    }

    // Convert back to EdgeCase list, preserving counts
    val aggregatedEdgeCases = edgeCaseCounts.map { (type, count) ->
        EdgeCase(type, count > 0)  // temporarily mark as covered if count > 0
    }

    return CoverageMetrics(
        methodsCovered = this.methodsCovered + result.methodsCovered,
        edgeCasesCovered = aggregatedEdgeCases,
        boundaryTests = this.boundaryTests + result.boundaryTests,
        edgeCasesCount = this.edgeCasesCount + result.edgeCasesCount,
        boundaryTestsCount = this.boundaryTestsCount + result.boundaryTestsCount
    )
}

fun CoverageMetrics.applyThreshold(totalFiles: Int): CoverageMetrics {
    // Count occurrences of each edge case type
    val edgeCaseCounts = mutableMapOf<String, Int>()

    edgeCasesCovered.forEach { edgeCase ->
        if (edgeCase.covered) {
            edgeCaseCounts[edgeCase.type] = edgeCaseCounts.getOrDefault(edgeCase.type, 0) + 1
        }
    }

    // Apply threshold to edge cases
    val thresholdedEdgeCases = edgeCaseCounts.map { (type, count) ->
        EdgeCase(type, (count.toDouble() / totalFiles) >= 0.7)
    }

    return CoverageMetrics(
        methodsCovered = this.methodsCovered,
        edgeCasesCovered = thresholdedEdgeCases,
        boundaryTests = this.boundaryTests,
        edgeCasesCount = this.edgeCasesCount,
        boundaryTestsCount = this.boundaryTestsCount
    )
}