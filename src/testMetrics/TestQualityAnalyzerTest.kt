package testMetrics

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TestQualityAnalyzerTest {
    private val analyzer = TestQualityAnalyzer()

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