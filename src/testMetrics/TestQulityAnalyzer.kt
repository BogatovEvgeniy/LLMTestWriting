package testMetrics

import testMetrics.analyzer.*

class TestQualityAnalyzer(
    private val basicAnalyzer: BasicMetricsAnalyzer,
    private val coverageAnalyzer: CoverageAnalyzer,
    private val qualityAnalyzer: QualityAnalyzer,
    private val readabilityAnalyzer: ReadabilityAnalyzer
) {

    fun analyzeBasicMetrics(test: String): BasicMetrics {
        val totalTests = countAnnotations(test, "@Test")
        val totalAssertions = countAssertions(test)
        val averageAssertions = if (totalTests > 0) totalAssertions.toDouble() / totalTests else 0.0

        return BasicMetrics(
            totalTests = totalTests,
            totalAssertions = totalAssertions,
            averageAssertionsPerTest = averageAssertions,
            score = TODO()
        )
    }

    fun analyzeCoverage(originalCode: String, test: String): CoverageMetrics {
        val startTime = System.currentTimeMillis()

        val methods = extractMethods(originalCode)
        val testedMethods = findTestedMethods(test, methods)
        val edgeCases = analyzeEdgeCases(test)
        val boundaryTests = findBoundaryTests(test)

        // Аналіз покриття коду
        val coverageAnalysis = analyzeCoverageDetails(originalCode, test)

        return CoverageMetrics(
            methodsCovered = testedMethods,
            edgeCasesCovered = edgeCases,
            boundaryTests = boundaryTests,
            generationTimeMs = System.currentTimeMillis() - startTime,
            lineCoverage = coverageAnalysis.lineCoverage,
            branchCoverage = coverageAnalysis.branchCoverage,
            conditionCoverage = coverageAnalysis.conditionCoverage,
            statementCoverage = coverageAnalysis.statementCoverage,
            pathCoverage = coverageAnalysis.pathCoverage
        )
    }

    private data class CodeCoverageAnalysis(
        val lineCoverage: Double,
        val branchCoverage: Double,
        val conditionCoverage: Double,
        val statementCoverage: Double,
        val pathCoverage: Double
    )

    private fun analyzeCoverageDetails(originalCode: String, test: String): CodeCoverageAnalysis {
        // Аналіз покриття рядків (CC)
        val lineCoverage = analyzeLineCoverage(originalCode, test)

        // Аналіз покриття гілок (BC)
        val branchCoverage = analyzeBranchCoverage(originalCode, test)

        // Аналіз покриття умов (CoC)
        val conditionCoverage = analyzeConditionCoverage(originalCode, test)

        // Аналіз покриття операторів (SC)
        val statementCoverage = analyzeStatementCoverage(originalCode, test)

        // Аналіз покриття шляхів (PC)
        val pathCoverage = analyzePathCoverage(originalCode, test)

        return CodeCoverageAnalysis(
            lineCoverage = lineCoverage,
            branchCoverage = branchCoverage,
            conditionCoverage = conditionCoverage,
            statementCoverage = statementCoverage,
            pathCoverage = pathCoverage
        )
    }

    private fun analyzeLineCoverage(originalCode: String, test: String): Double {
        // Підрахунок рядків коду та їх покриття тестами
        val codeLines = originalCode.lines().filter { it.trim().isNotEmpty() }
        var coveredLines = 0

        codeLines.forEach { line ->
            if (isLineCovered(line, test)) {
                coveredLines++
            }
        }

        return coveredLines.toDouble() / codeLines.size
    }

    private fun analyzeBranchCoverage(originalCode: String, test: String): Double {
        // Аналіз розгалужень (if, when, etc.)
        val branches = findBranches(originalCode)
        var coveredBranches = 0

        branches.forEach { branch ->
            if (isBranchCovered(branch, test)) {
                coveredBranches++
            }
        }

        return if (branches.isEmpty()) 1.0
        else coveredBranches.toDouble() / branches.size
    }

    private fun analyzeConditionCoverage(originalCode: String, test: String): Double {
        // Аналіз умов в розгалуженнях
        val conditions = findConditions(originalCode)
        var coveredConditions = 0

        conditions.forEach { condition ->
            if (isConditionCovered(condition, test)) {
                coveredConditions++
            }
        }

        return if (conditions.isEmpty()) 1.0
        else coveredConditions.toDouble() / conditions.size
    }

    private fun analyzeStatementCoverage(originalCode: String, test: String): Double {
        // Аналіз покриття операторів
        val statements = findStatements(originalCode)
        var coveredStatements = 0

        statements.forEach { statement ->
            if (isStatementCovered(statement, test)) {
                coveredStatements++
            }
        }

        return if (statements.isEmpty()) 1.0
        else coveredStatements.toDouble() / statements.size
    }

    private fun analyzePathCoverage(originalCode: String, test: String): Double {
        // Аналіз покриття можливих шляхів виконання
        val paths = findPaths(originalCode)
        var coveredPaths = 0

        paths.forEach { path ->
            if (isPathCovered(path, test)) {
                coveredPaths++
            }
        }

        return if (paths.isEmpty()) 1.0
        else coveredPaths.toDouble() / paths.size
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

    private fun isLineCovered(line: String, test: String): Boolean {
        // Видаляємо пробіли та коментарі для порівняння
        val cleanLine = line.replace("\\s+".toRegex(), "")
            .replace("//.*".toRegex(), "")

        // Шукаємо використання методів або перевірку значень з цього рядка
        return test.contains(cleanLine) ||
                hasAssertionForLine(cleanLine, test) ||
                hasMethodCallForLine(cleanLine, test)
    }

    private fun hasAssertionForLine(line: String, test: String): Boolean {
        // Шукаємо assert-твердження, які перевіряють цей рядок
        val assertPattern = "assert.*\\($line.*\\)".toRegex()
        return test.contains(assertPattern)
    }

    private fun hasMethodCallForLine(line: String, test: String): Boolean {
        // Видобуваємо імена методів з рядка
        val methodPattern = "\\b\\w+\\(".toRegex()
        val methods = methodPattern.findAll(line).map { it.value.removeSuffix("(") }

        return methods.any { method ->
            test.contains(method)
        }
    }

    private fun findBranches(code: String): List<String> {
        val branchPattern = """(if|when|else|else\s+if)\s*\([^)]*\)""".toRegex()
        return branchPattern.findAll(code).map { it.value }.toList()
    }

    private fun isBranchCovered(branch: String, test: String): Boolean {
        val condition = branch
            .replace("if\\s*\\(".toRegex(), "")
            .replace("\\).*".toRegex(), "")
            .trim()

        return test.contains("test.*$condition".toRegex())
    }

    private fun findConditions(code: String): List<String> {
        val conditionRegex = """(if|when)\s*\(([^)]*)\)""".toRegex()
        return conditionRegex.findAll(code)
            .map { it.groupValues[2].trim() }
            .toList()
    }

    private fun isConditionCovered(condition: String, test: String): Boolean {
        return test.contains(condition)
    }

    private fun findStatements(code: String): List<String> {
        val statementRegex = """[^;{}]+;""".toRegex()
        return statementRegex.findAll(code)
            .map { it.value.trim() }
            .toList()
    }

    private fun isStatementCovered(statement: String, test: String): Boolean {
        return test.contains(statement)
    }

    private fun findPaths(code: String): List<String> {
        val branches = findBranches(code)
        return branches.map { branch ->
            branch.replace("if\\s*\\(".toRegex(), "")
                .replace("\\).*".toRegex(), "")
                .trim()
        }
    }

    private fun isPathCovered(path: String, test: String): Boolean {
        return test.contains(path)
    }

    fun analyzeTest(originalCode: String, testCode: String): TestAnalysisResult {
        TODO()
    }
}