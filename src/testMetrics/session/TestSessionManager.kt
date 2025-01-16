package testMetrics.session

import generator.Generators
import java.io.File

class TestSessionManager(private val basePath: String) {
    fun createSession(generators: List<Generators>): TestSessionImpl {
        val sessionId = getNextSessionId()
        return TestSessionImpl(
            sessionId = sessionId,
            timestamp = System.currentTimeMillis(),
            generators = generators,
            basePath = basePath
        )
    }

    fun getSession(sessionId: Int): TestSessionImpl? {
        return if (sessionExists(sessionId)) {
            TestSessionImpl(
                sessionId = sessionId,
                timestamp = getSessionTimestamp(sessionId),
                generators = getSessionGenerators(sessionId),
                basePath = basePath
            )
        } else null
    }

    private fun getNextSessionId(): Int {
        // Your existing getNextSessionNumber logic here
        val resultDirs = listOf("result/gpt", "result/gemini", "result/codellama")
        var maxSession = 0

        resultDirs.forEach { dirPath ->
            val dir = File(basePath, dirPath)
            if (dir.exists()) {
                dir.listFiles { file ->
                    file.isDirectory && file.name.startsWith("session")
                }?.forEach { sessionDir ->
                    val sessionNum = sessionDir.name.removePrefix("session").toIntOrNull() ?: 0
                    if (sessionNum > maxSession) {
                        maxSession = sessionNum
                    }
                }
            }
        }

        return maxSession + 1
    }

    private fun sessionExists(sessionId: Int): Boolean {
        // Check if any generator directory has this session
        return listOf("gpt", "gemini", "codellama").any { generator ->
            File("$basePath/result/$generator/session$sessionId").exists()
        }
    }

    private fun getSessionTimestamp(sessionId: Int): Long {
        // Get creation time of the first found session directory
        return listOf("gpt", "gemini", "codellama")
            .map { File("$basePath/result/$it/session$sessionId") }
            .firstOrNull { it.exists() }
            ?.lastModified() ?: System.currentTimeMillis()
    }

    private fun getSessionGenerators(sessionId: Int): List<Generators> {
        // Find which generators have results for this session
        return listOf("gpt" to Generators.GPT,
            "gemini" to Generators.GEMINI,
            "codellama" to Generators.CODELLAMA)
            .filter { (path, _) ->
                File("$basePath/result/$path/session$sessionId").exists()
            }
            .map { it.second }
    }
}