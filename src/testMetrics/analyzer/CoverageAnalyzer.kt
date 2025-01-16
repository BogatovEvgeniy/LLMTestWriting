package testMetrics.analyzer

import testMetrics.parser.*

class CoverageAnalyzer : MetricsAnalyzer<CoverageMetrics> {
    private val codeParser = CodeParser() // Remove code dependency

    override fun analyze(code: String, test: String): CoverageMetrics {
        val parseResult = codeParser.parse(code)
        val startTime = System.currentTimeMillis()

        return CoverageMetrics(
            methodsCovered = findTestedMethods(code, test),
            edgeCasesCovered = analyzeEdgeCases(test),
            boundaryTests = findBoundaryTests(test),
            generationTimeMs = System.currentTimeMillis() - startTime,
            lineCoverage = analyzeLineCoverage(code, test),
            branchCoverage = analyzeBranchCoverage(parseResult.branches, test),
            conditionCoverage = analyzeConditionCoverage(parseResult.conditions, test),
            statementCoverage = analyzeStatementCoverage(parseResult.statements, test),
            pathCoverage = analyzePathCoverage(parseResult.paths, test)
        )
    }

    private fun analyzeLineCoverage(code: String, test: String): Double {
        val codeLines = code.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("//") }

        val coveredLines = codeLines.count { line ->
            isLineCovered(line, test)
        }

        return if (codeLines.isNotEmpty()) {
            coveredLines.toDouble() / codeLines.size
        } else 1.0
    }

    private fun isLineCovered(line: String, test: String): Boolean {
        val cleanLine = line.replace("\\s+".toRegex(), "")
            .replace("//.*".toRegex(), "")

        return test.contains(cleanLine) ||
                hasAssertionForLine(cleanLine, test) ||
                hasMethodCallForLine(cleanLine, test)
    }

    private fun hasAssertionForLine(line: String, test: String): Boolean {
        val assertPattern = "assert.*\\($line.*\\)".toRegex()
        return test.contains(assertPattern)
    }

    private fun hasMethodCallForLine(line: String, test: String): Boolean {
        val methodPattern = "\\b\\w+\\(".toRegex()
        val methods = methodPattern.findAll(line)
            .map { it.value.removeSuffix("(") }

        return methods.any { test.contains(it) }
    }

    private fun analyzeBranchCoverage(branches: List<Branch>, test: String): Double {
        if (branches.isEmpty()) return 1.0

        val coveredBranches = branches.count { branch ->
            isBranchCovered(branch, test)
        }

        return coveredBranches.toDouble() / branches.size
    }

    private fun isBranchCovered(branch: Branch, test: String): Boolean {
        val condition = branch.content
            .replace("if\\s*\\(".toRegex(), "")
            .replace("\\).*".toRegex(), "")
            .trim()

        val hasPositiveTest = test.contains("test.*${condition}.*true".toRegex())
        val hasNegativeTest = test.contains("test.*${condition}.*false".toRegex())

        return hasPositiveTest && hasNegativeTest
    }

    private fun analyzeConditionCoverage(conditions: List<Condition>, test: String): Double {
        if (conditions.isEmpty()) return 1.0

        val coveredConditions = conditions.count { condition ->
            isConditionCovered(condition, test)
        }

        return coveredConditions.toDouble() / conditions.size
    }

    private fun isConditionCovered(condition: Condition, test: String): Boolean {
        val content = condition.content.trim()

        return when (condition.type) {
            ConditionType.EQUALITY -> {
                test.contains("assertEquals") && test.contains(content)
            }
            ConditionType.INEQUALITY -> {
                test.contains("assertNotEquals") && test.contains(content)
            }
            ConditionType.GREATER_THAN, ConditionType.LESS_THAN -> {
                test.contains(content) && test.contains("assert.*[<>]".toRegex())
            }
            ConditionType.CONTAINS -> {
                test.contains(content) &&
                        (test.contains("contains") || test.contains("assertTrue"))
            }
            ConditionType.OTHER -> test.contains(content)
        }
    }

    private fun analyzeStatementCoverage(statements: List<Statement>, test: String): Double {
        if (statements.isEmpty()) return 1.0

        val coveredStatements = statements.count { statement ->
            isStatementCovered(statement, test)
        }

        return coveredStatements.toDouble() / statements.size
    }

    private fun isStatementCovered(statement: Statement, test: String): Boolean {
        val content = statement.content.trim()

        return when (statement.type) {
            StatementType.ASSIGNMENT -> {
                val variable = content.substringBefore("=").trim()
                test.contains("assert.*$variable".toRegex())
            }
            StatementType.METHOD_CALL -> {
                val methodName = content.substringBefore("(")
                test.contains(methodName)
            }
            StatementType.RETURN -> {
                val returnValue = content.substringAfter("return").trim()
                test.contains("assertEquals.*$returnValue".toRegex()) ||
                        test.contains("assertThat.*$returnValue".toRegex())
            }
            StatementType.OTHER -> test.contains(content)
        }
    }

    private fun analyzePathCoverage(paths: List<Path>, test: String): Double {
        if (paths.isEmpty()) return 1.0

        val coveredPaths = paths.count { path ->
            isPathCovered(path, test)
        }

        return coveredPaths.toDouble() / paths.size
    }

    private fun isPathCovered(path: Path, test: String): Boolean {
        return path.branches.all { branch ->
            val condition = branch.content
                .replace("if\\s*\\(".toRegex(), "")
                .replace("\\).*".toRegex(), "")
                .trim()

            test.contains("test.*$condition".toRegex()) &&
                    test.contains(branch.content)
        }
    }

    private fun findTestedMethods(code: String, test: String): Set<String> {
        val methodPattern = "fun\\s+(\\w+)\\s*\\(".toRegex()
        val methods = methodPattern.findAll(code)
            .map { it.groupValues[1] }
            .toSet()

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
}