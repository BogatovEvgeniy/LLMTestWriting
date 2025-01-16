package testMetrics.parser

class CodeParser(private val code: String) {
    data class ParseResult(
        val branches: List<Branch>,
        val conditions: List<Condition>,
        val statements: List<Statement>,
        val paths: List<Path>
    )

    fun parse(code: String): ParseResult {
        return ParseResult(
            branches = findBranches(),
            conditions = findConditions(),
            statements = findStatements(),
            paths = findPaths()
        )
    }

    private fun findBranches(): List<Branch> {
        val branchRegex = """(if|when|else|else\s+if)\s*\([^)]*\)""".toRegex()
        return branchRegex.findAll(code).map { match ->
            Branch(
                content = match.value,
                startIndex = match.range.first,
                endIndex = findClosingBrace(code, match.range.last + 1)
            )
        }.toList()
    }

    private fun findConditions(): List<Condition> {
        val conditionRegex = """(if|when)\s*\(([^)]*)\)""".toRegex()
        return conditionRegex.findAll(code).map { match ->
            val conditions = match.groupValues[2].split("&&|\\|\\|".toRegex())
            conditions.map { condition ->
                Condition(
                    content = condition.trim(),
                    type = determineConditionType(condition)
                )
            }
        }.flatten().toList()
    }

    private fun findStatements(): List<Statement> {
        val statementRegex = """[^;{}]+;""".toRegex()
        return statementRegex.findAll(code).map { match ->
            Statement(
                content = match.value,
                type = determineStatementType(match.value)
            )
        }.toList()
    }

    private fun findPaths(): List<Path> {
        val paths = mutableListOf<Path>()
        val branches = findBranches()

        // Простий алгоритм для знаходження шляхів
        var currentPath = mutableListOf<Branch>()
        findPathsRecursive(branches, currentPath, paths)

        return paths
    }

    private fun findPathsRecursive(
        branches: List<Branch>,
        currentPath: MutableList<Branch>,
        allPaths: MutableList<Path>
    ) {
        if (branches.isEmpty()) {
            if (currentPath.isNotEmpty()) {
                allPaths.add(Path(currentPath.toList()))
            }
            return
        }

        val branch = branches.first()
        val remainingBranches = branches.drop(1)

        // Додаємо шлях з поточною гілкою
        currentPath.add(branch)
        findPathsRecursive(remainingBranches, currentPath, allPaths)
        currentPath.removeAt(currentPath.size - 1)

        // Додаємо шлях без поточної гілки
        findPathsRecursive(remainingBranches, currentPath, allPaths)
    }

    private fun findClosingBrace(code: String, startIndex: Int): Int {
        var braceCount = 1
        var currentIndex = startIndex

        while (currentIndex < code.length && braceCount > 0) {
            when (code[currentIndex]) {
                '{' -> braceCount++
                '}' -> braceCount--
            }
            currentIndex++
        }

        return currentIndex
    }

    private fun determineConditionType(condition: String): ConditionType {
        return when {
            condition.contains("==") -> ConditionType.EQUALITY
            condition.contains(">") -> ConditionType.GREATER_THAN
            condition.contains("<") -> ConditionType.LESS_THAN
            condition.contains("!=") -> ConditionType.INEQUALITY
            condition.contains("in") -> ConditionType.CONTAINS
            else -> ConditionType.OTHER
        }
    }

    private fun determineStatementType(statement: String): StatementType {
        return when {
            statement.contains("=") -> StatementType.ASSIGNMENT
            statement.contains(".") -> StatementType.METHOD_CALL
            statement.contains("return") -> StatementType.RETURN
            else -> StatementType.OTHER
        }
    }
}