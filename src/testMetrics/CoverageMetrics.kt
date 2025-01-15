package testMetrics

data class CoverageMetrics(
    val methodsCovered: Set<String>,
    val edgeCasesCovered: List<EdgeCase>,
    val boundaryTests: List<String>
)