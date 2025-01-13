package generator

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object CopilotTestGenerator : TestGenerator {

    override fun generateTestsFor(llmPrompt: String, code: String): String {
        return "import org.junit.jupiter.api.Assertions.*\n" +
                "import org.junit.jupiter.api.Test\n" +
                "\n" +
                "internal class FibonacciGeneratorTest {\n" +
                "\n" +
                "    @Test\n" +
                "    fun `test Fibonacci sequence generation`() {\n" +
                "        val limit = 100\n" +
                "        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89)\n" +
                "        val generator = FibonacciGenerator(limit)\n" +
                "        val actualSequence = generator.getSequence()\n" +
                "\n" +
                "        assertEquals(expectedSequence, actualSequence)\n" +
                "    }\n" +
                "\n" +
                "    @Test\n" +
                "    fun `test number is a Fibonacci number`() {\n" +
                "        val generator = FibonacciGenerator(100)\n" +
                "        assertTrue(generator.isFibonacciNumber(55))\n" +
                "    }\n" +
                "\n" +
                "    @Test\n" +
                "    fun `test number is not a Fibonacci number`() {\n" +
                "        val generator = FibonacciGenerator(100)\n" +
                "        assertFalse(generator.isFibonacciNumber(4))\n" +
                "    }\n" +
                "\n" +
                "    @Test\n" +
                "    fun `test edge case - 0 is a Fibonacci number`() {\n" +
                "        val generator = FibonacciGenerator(100)\n" +
                "        assertTrue(generator.isFibonacciNumber(0))\n" +
                "    }\n" +
                "\n" +
                "    @Test\n" +
                "    fun `test edge case - 1 is a Fibonacci number`() {\n" +
                "        val generator = FibonacciGenerator(100)\n" +
                "        assertTrue(generator.isFibonacciNumber(1))\n" +
                "    }\n" +
                "\n" +
                "    @Test\n" +
                "    fun `test edge case - limit is not exceeded`() {\n" +
                "        val limit = 89\n" +
                "        val generator = FibonacciGenerator(limit)\n" +
                "        val sequence = generator.getSequence()\n" +
                "\n" +
                "        assertFalse(sequence.contains(limit + 1))\n" +
                "    }\n" +
                "}\n"
    }
}
