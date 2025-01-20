package testMetrics

import coordinator.TestGenerationConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TestQualityAnalyzerTest {
    private val testConfig = TestGenerationConfig(
        basicMetricsWeight = 0.3,
        coverageMetricsWeight = 0.3,
        qualityMetricsWeight = 0.2,
        readabilityMetricsWeight = 0.2,
        timingMetricsWeight = 0.0,
        useGPT = true,
        useGemini = false,
        useCodeLlama = false,
        prompt = ""
    )

    // Add this line to initialize the analyzer
    private val analyzer = TestQualityAnalyzer(testConfig)

    @Test
    fun `test basic metrics calculation`() {
        val testCode = """
            @Test
            fun `test something`() {
                assertEquals(1, 1)
                assertTrue(true)
            }
            
            @Test
            fun `test another thing`() {
                assertFalse(false)
            }
        """.trimIndent()

        val metrics = analyzer.analyzeTest("", testCode)

        assertEquals(2, metrics.basicMetrics.totalTests)
        assertEquals(3, metrics.basicMetrics.totalAssertions)
        assertEquals(1.5, metrics.basicMetrics.averageAssertionsPerTest)
    }

    // Додати інші тести для перевірки різних аспектів аналізу
}