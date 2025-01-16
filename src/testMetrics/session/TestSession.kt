package testMetrics.session

import generator.Generators
import java.io.File

class TestSession(
    val sessionId: Int,
    val timestamp: Long,
    val generators: List<Generators>,
    private val basePath: String
) {
    fun saveResults(results: Map<Generators, String>) {
        results.forEach { (generator, test) ->
            val path = getGeneratorPath(generator)
            val sessionDir = File(basePath, "$path/session$sessionId")
            sessionDir.mkdirs()

            // Save test with original filename
            test.lines().forEach { line ->
                if (line.startsWith("class ")) {
                    val fileName = line.split(" ")[1].split("{")[0] + ".kt"
                    File(sessionDir, fileName).writeText(test)
                }
            }
        }
    }

    fun loadResults(): Map<Generators, String> {
        return generators.mapNotNull { generator ->
            val sessionDir = File(basePath, "${getGeneratorPath(generator)}/session$sessionId")
            if (sessionDir.exists() && sessionDir.isDirectory) {
                val tests = sessionDir.listFiles()
                    ?.filter { it.extension == "kt" }
                    ?.joinToString("\n") { it.readText() }
                if (!tests.isNullOrEmpty()) {
                    generator to tests
                } else null
            } else null
        }.toMap()
    }

    private fun getGeneratorPath(generator: Generators): String {
        return when(generator) {
            Generators.GPT -> "result/gpt"
            Generators.GEMINI -> "result/gemini"
            Generators.CODELLAMA -> "result/codellama"
            else -> "result/other"
        }
    }
}