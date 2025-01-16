package testMetrics.analyzer

import testMetrics.analyzer.EdgeCase

data class CoverageMetrics(
    val methodsCovered: Set<String>,
    val edgeCasesCovered: List<EdgeCase>,
    val boundaryTests: List<String>,
    val generationTimeMs: Long,
    val lineCoverage: Double,
    val branchCoverage: Double,
    val conditionCoverage: Double,
    val statementCoverage: Double,
    val pathCoverage: Double,
    override val score: Double = calculateScore(
        lineCoverage,
        branchCoverage,
        conditionCoverage,
        statementCoverage,
        pathCoverage
    )
) : Metrics {
    companion object {
        private fun calculateScore(
            lineCoverage: Double,
            branchCoverage: Double,
            conditionCoverage: Double,
            statementCoverage: Double,
            pathCoverage: Double
        ): Double {
            return (lineCoverage * 20 +
                    branchCoverage * 20 +
                    conditionCoverage * 20 +
                    statementCoverage * 20 +
                    pathCoverage * 20)
        }
    }
}