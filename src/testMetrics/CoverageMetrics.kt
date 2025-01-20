package testMetrics

data class CoverageMetrics(
    val methodsCovered: Int = 0,
    val edgeCasesCovered: List<EdgeCase>,
    val boundaryTests: List<String>,
    val edgeCasesCount: Int = 0,
    val boundaryTestsCount: Int = 0
)