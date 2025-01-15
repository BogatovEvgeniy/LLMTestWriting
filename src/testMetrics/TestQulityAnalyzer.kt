package testMetrics

class TestQualityAnalyzer {
    fun analyzeTest(originalCode: String, generatedTest: String): TestAnalysisResult {
        return TestAnalysisResult(
            basicMetrics = analyzeBasicMetrics(generatedTest),
            coverageMetrics = analyzeCoverage(originalCode, generatedTest),
            qualityMetrics = analyzeQuality(generatedTest),
            readabilityMetrics = analyzeReadability(generatedTest)
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
        val edgeCases = analyzeEdgeCases(test)
        val boundaryTests = findBoundaryTests(test)

        return CoverageMetrics(
            methodsCovered = testedMethods,
            edgeCasesCovered = edgeCases,
            boundaryTests = boundaryTests
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
        val methodPattern = "fun\\s+(\\w+)\\s*\\(".toRegex()
        return methodPattern.findAll(code)
            .map { it.groupValues[1] }
            .toSet()
    }

    private fun findTestedMethods(test: String, methods: Set<String>): Set<String> {
        return methods.filter { method ->
            test.contains(method)
        }.toSet()
    }

    private fun analyzeEdgeCases(test: String): List<EdgeCase> {
        return listOf(
            EdgeCase("zero", test.contains("test.*0")),
            EdgeCase("negative", test.contains("test.*negative")),
            EdgeCase("empty", test.contains("test.*empty")),
            EdgeCase("null", test.contains("test.*null")),
            EdgeCase("large_values", test.contains("test.*large"))
        )
    }

    private fun findBoundaryTests(test: String): List<String> {
        val boundaryPattern = "test.*(?:boundary|limit|max|min).*".toRegex(RegexOption.IGNORE_CASE)
        return boundaryPattern.findAll(test)
            .map { it.value }
            .toList()
    }

    private fun checkDescriptiveNames(test: String): Boolean {
        val testNamePattern = "@Test\\s*fun\\s+`([^`]+)`".toRegex()
        val testNames = testNamePattern.findAll(test)
            .map { it.groupValues[1] }
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
        val testMethodPattern = "fun\\s+`?\\w+`?\\s*\\(".toRegex()
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