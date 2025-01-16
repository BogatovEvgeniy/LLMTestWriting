package testMetrics.analyzer

class QualityAnalyzer : MetricsAnalyzer<QualityMetrics> {
    override fun analyze(code: String, test: String): QualityMetrics {
        return QualityMetrics(
            hasDescriptiveNames = checkDescriptiveNames(test),
            usesAssertionVariety = checkAssertionVariety(test),
            hasTestDocumentation = hasTestDocumentation(test),
            followsNamingConventions = checkNamingConventions(test)
        )
    }

    private fun checkDescriptiveNames(test: String): Boolean {
        // Look for test method names in backticks
        val testNamePattern = "@Test\\s*fun\\s+`([^`]+)`".toRegex()
        val testNames = testNamePattern.findAll(test)
            .map { it.groupValues[1] } // Get the name between backticks
            .toList()

        return testNames.isNotEmpty() && testNames.all { name ->
            // Check if name has at least 3 words and is descriptive
            val words = name.split(" ")
            words.size >= 3 &&
                    words.none { it.length < 2 } && // No single-letter words
                    !name.contains("test1", ignoreCase = true) // No numbered tests
        }
    }

    private fun checkAssertionVariety(test: String): Boolean {
        val assertTypes = setOf(
            "assertEquals",
            "assertTrue",
            "assertFalse",
            "assertNull",
            "assertNotNull",
            "assertThrows",
            "assertThat"
        )

        // Count different types of assertions used
        val usedAssertTypes = assertTypes.count { assertType ->
            test.contains(assertType)
        }

        // Require at least 3 different types of assertions
        return usedAssertTypes >= 3
    }

    private fun hasTestDocumentation(test: String): Boolean {
        // Check for both KDoc and line comments
        val hasKDoc = test.contains("/**") && test.contains("*/")
        val hasLineComments = test.lines()
            .any { it.trim().startsWith("//") }

        // Check for descriptive comments before test methods
        val hasTestDescriptions = test.contains("""
           /\*\*
            *[^*]+
            \*/
           \s*@Test
       """.trimIndent().toRegex())

        return hasKDoc || hasLineComments || hasTestDescriptions
    }

    private fun checkNamingConventions(test: String): Boolean {
        // Check test method names
        val methodPattern = "fun\\s+`?\\w+`?\\s*\\(".toRegex()
        val testMethods = methodPattern.findAll(test)
            .map { it.value }
            .toList()

        return testMethods.isNotEmpty() && testMethods.all { method ->
            // Check if method name:
            // 1. Uses backticks
            // 2. Follows naming convention (lowercase with spaces)
            val usesBackticks = method.contains("`")
            val name = method.substringAfter("`").substringBefore("`")
            val followsConvention = name.matches("[a-z\\s]+".toRegex())

            usesBackticks && followsConvention
        }
    }

    private fun isTestClassWellStructured(test: String): Boolean {
        // Check for proper test class structure
        val hasSetup = test.contains("@BeforeEach") || test.contains("@BeforeAll")
        val hasTeardown = test.contains("@AfterEach") || test.contains("@AfterAll")
        val hasGrouping = test.contains("@Nested") || test.contains("@DisplayName")

        // At least one of these structural elements should be present
        return hasSetup || hasTeardown || hasGrouping
    }

    private fun checkMethodOrganization(test: String): Boolean {
        val testMethods = test.split("@Test")
            .drop(1) // Skip content before first test

        // Check if related tests are grouped together
        var currentGroup = ""
        var isOrganized = true

        testMethods.forEach { method ->
            val methodContent = method.trim()
            val methodPurpose = extractTestPurpose(methodContent)

            if (currentGroup.isEmpty()) {
                currentGroup = methodPurpose
            } else if (!methodPurpose.startsWith(currentGroup)) {
                // Check if this is start of new group
                if (testMethods.any { it.contains(methodPurpose) }) {
                    currentGroup = methodPurpose
                } else {
                    isOrganized = false
                }
            }
        }

        return isOrganized
    }

    private fun extractTestPurpose(methodContent: String): String {
        // Extract main purpose from test name or first comment
        val nameMatch = "`([^`]+)`".toRegex().find(methodContent)
        return nameMatch?.groupValues?.get(1)?.split(" ")?.first() ?: ""
    }
}