package testMetrics.analyzer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class CoverageAnalyzerTest {

    @Test
    fun `test line coverage calculation`() {
        val originalCode = """
            fun sum(a: Int, b: Int): Int {
                return a + b
            }
        """.trimIndent()

        val testCode = """
            @Test
            fun `test sum`() {
                assertEquals(3, sum(1, 2))
            }
        """.trimIndent()

        val analyzer = CoverageAnalyzer(originalCode, testCode)
        val coverage = analyzer.analyzeCoverage()

        assertTrue(coverage.lineCoverage > 0.0)
        assertEquals(1, coverage.methodsCovered.size)
    }

    @Test
    fun `test branch coverage calculation`() {
        val originalCode = """
            fun max(a: Int, b: Int): Int {
                return if (a > b) a else b
            }
        """.trimIndent()

        val testCode = """
            @Test
            fun `test max with first greater`() {
                assertEquals(2, max(2, 1))
            }
            
            @Test
            fun `test max with second greater`() {
                assertEquals(2, max(1, 2))
            }
        """.trimIndent()

        val analyzer = CoverageAnalyzer(originalCode, testCode)
        val coverage = analyzer.analyzeCoverage()

        assertEquals(1.0, coverage.branchCoverage)
    }

    @Test
    fun `test edge cases detection`() {
        val originalCode = """
            fun process(list: List<Int>): Int {
                return list.sum()
            }
        """.trimIndent()

        val testCode = """
            @Test
            fun `test process with empty list`() {
                assertEquals(0, process(emptyList()))
            }
            
            @Test
            fun `test process with null`() {
                assertThrows(NullPointerException::class.java) {
                    process(null)
                }
            }
        """.trimIndent()

        val analyzer = CoverageAnalyzer(originalCode, testCode)
        val coverage = analyzer.analyzeCoverage()

        assertTrue(coverage.edgeCasesCovered.any { it.type == "empty" && it.covered })
        assertTrue(coverage.edgeCasesCovered.any { it.type == "null" && it.covered })
    }
}