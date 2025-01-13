import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FibonacciGeneratorTest {

    @Test
    fun `test Fibonacci sequence generation`() {
        val limit = 100
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89)
        val generator = FibonacciGenerator(limit)
        val actualSequence = generator.getSequence()

        assertEquals(expectedSequence, actualSequence)
    }

    @Test
    fun `test number is a Fibonacci number`() {
        val generator = FibonacciGenerator(100)
        assertTrue(generator.isFibonacciNumber(55))
    }

    @Test
    fun `test number is not a Fibonacci number`() {
        val generator = FibonacciGenerator(100)
        assertFalse(generator.isFibonacciNumber(4))
    }

    @Test
    fun `test edge case - 0 is a Fibonacci number`() {
        val generator = FibonacciGenerator(100)
        assertTrue(generator.isFibonacciNumber(0))
    }

    @Test
    fun `test edge case - 1 is a Fibonacci number`() {
        val generator = FibonacciGenerator(100)
        assertTrue(generator.isFibonacciNumber(1))
    }

    @Test
    fun `test edge case - limit is not exceeded`() {
        val limit = 89
        val generator = FibonacciGenerator(limit)
        val sequence = generator.getSequence()

        assertFalse(sequence.contains(limit + 1))
    }
}
