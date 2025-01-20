package testMetrics

import coordinator.TestGenerationConfig

class TestQualityAnalyzer(private val config: TestGenerationConfig) {
    fun analyzeTest(originalCode: String, generatedTest: String): TestAnalysisResult {
        return TestAnalysisResult(
            basicMetrics = analyzeBasicMetrics(generatedTest),
            coverageMetrics = analyzeCoverage(originalCode, generatedTest),
            qualityMetrics = analyzeQuality(generatedTest),
            readabilityMetrics = analyzeReadability(generatedTest),
            config = config
        )
    }

    private fun analyzeBasicMetrics(test: String): BasicMetrics {
        val totalTests = countAnnotations(test, "@Test")
        val totalAssertions = countAssertions(test)
        val averageAssertions = if(totalTests > 0) totalAssertions.toDouble() / totalTests else 0.0

        return BasicMetrics(
            totalTests = totalTests,
            totalAssertions = totalAssertions,
            averageAssertionsPerTest = averageAssertions
        )
    }

    private fun analyzeCoverage(originalCode: String, test: String): CoverageMetrics {
        val methods = extractMethods(originalCode)
        val testedMethods = findTestedMethods(test, methods)
        val edgeCases = analyzeEdgeCases(test, methods)
        val boundaryTests = findBoundaryTests(test, methods)

        // Calculate methods coverage by tests
        val methodsCoverage = testedMethods.size/methods.size
        // Calculate methods with edge cases and boundary tests
        val methodsWithEdgeCases = methods.count { method ->
            analyzeEdgeCases(test, setOf(method)).isNotEmpty()
        }
        val methodsWithBoundaryTests = methods.count { method ->
            findBoundaryTests(test, setOf(method)).isNotEmpty()
        }

        return CoverageMetrics(
            methodsCovered = methodsCoverage,
            edgeCasesCovered = edgeCases,
            boundaryTests = boundaryTests,
            edgeCasesCount = methodsWithEdgeCases,
            boundaryTestsCount = methodsWithBoundaryTests
        )
    }

    private fun analyzeQuality(test: String): QualityMetrics {
        return QualityMetrics(
            hasDescriptiveNames = checkDescriptiveNames(test),
            usesAssertionVariety = checkAssertionVariety(test),
            hasTestDocumentation = hasTestDocumentation(test),
            followsNamingConventions = checkNamingConventions(test)
        )
    }

    private fun analyzeReadability(test: String): ReadabilityMetrics {
        return ReadabilityMetrics(
            usesBackticks = test.contains("`"),
            hasComments = test.contains("//") || test.contains("/*"),
            averageTestLength = calculateAverageTestLength(test)
        )
    }

    private fun countAnnotations(test: String, annotation: String): Int {
        return test.split("\n")
            .count { it.trim().startsWith(annotation) }
    }

    private fun countAssertions(test: String): Int {
        return test.split("\n")
            .count { it.contains("assert") }
    }

    private fun extractMethods(code: String): Set<String> {
        val methodPattern = "(fun|void)\\s+(\\w+)\\s*\\(".toRegex()
        return methodPattern.findAll(code)
            .map { it.groupValues[2] }
            .toSet()
    }

    private fun findTestedMethods(test: String, methods: Set<String>): Set<String> {
        return methods.filter { method ->
            test.contains(method)
        }.toSet()
    }

    private fun analyzeEdgeCases(test: String, methods: Set<String>): List<EdgeCase> {
        return listOf(
            EdgeCase("zero", methods.any { method -> (test.contains(method) && (test.contains("0") || test.contains("zero"))) }),
            EdgeCase("negative", methods.any { method -> (test.contains(method) && (test.contains("negative") || test.contains("-"))) }),
            EdgeCase("empty", methods.any { method -> (test.contains(method) && (test.contains("empty") || test.contains("blank"))) }),
            EdgeCase("null", methods.any { method -> (test.contains(method) && (test.contains("null"))) }),
            EdgeCase("large_values", methods.any { method -> (test.contains(method) && (test.contains("large") || test.contains("big"))) }),
        ).filter { it.covered }
    }

    private fun findBoundaryTests(test: String, methods: Set<String>): List<String> {
        return methods.filter { method ->
            val boundaryPattern = ".*(?:boundary|limit|max|min).*".toRegex(RegexOption.IGNORE_CASE)
            test.contains(method) && boundaryPattern.containsMatchIn(test)
        }.toList()
    }

    private fun checkDescriptiveNames(test: String): Boolean {
        val testNamePattern = "@Test\\s*(fun|void)\\s+`([^`]+)`".toRegex()
        val testNames = testNamePattern.findAll(test)
            .map { it.groupValues[2] }
        return testNames.all { it.split(" ").size >= 3 }
    }

    private fun checkAssertionVariety(test: String): Boolean {
        val assertTypes = setOf(
            "assertEquals",
            "assertTrue",
            "assertFalse",
            "assertNull",
            "assertNotNull",
            "assertThrows"
        )
        val usedAsserts = assertTypes.count { test.contains(it) }
        return usedAsserts >= 3 // Використовується мінімум 3 різних типи assert
    }

    private fun hasTestDocumentation(test: String): Boolean {
        return test.contains("/**") || test.contains("//")
    }

    private fun checkNamingConventions(test: String): Boolean {
        val testMethodPattern = "(fun|void)\\s+`?\\w+`?\\s*\\(".toRegex()
        val methods = testMethodPattern.findAll(test)
        return methods.all { it.value.contains("`") }
    }

    private fun calculateAverageTestLength(test: String): Int {
        val testBlocks = test.split("@Test")
        if (testBlocks.size <= 1) return 0

        val lengths = testBlocks.drop(1)
            .map { it.lines().count() }
        return lengths.average().toInt()
    }
}