Certainly! Below is a set of unit tests for the `FibonacciGenerator` class written in Kotlin. The tests cover the basic functionality of generating Fibonacci numbers up to a specified limit and checking if a number is part of the Fibonacci sequence. You can use a testing framework like JUnit for this purpose.

```kotlin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {

    private lateinit var fibonacciGenerator: FibonacciGenerator

    @BeforeEach
    fun setUp() {
        // You can set up different limits for each test as needed
    }

    @Test
    fun `test sequence generation up to limit 0`() {
        fibonacciGenerator = FibonacciGenerator(0)
        val expectedSequence = listOf(0, 1)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 1`() {
        fibonacciGenerator = FibonacciGenerator(1)
        val expectedSequence = listOf(0, 1)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 2`() {
        fibonacciGenerator = FibonacciGenerator(2)
        val expectedSequence = listOf(0, 1, 1, 2)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 10`() {
        fibonacciGenerator = FibonacciGenerator(10)
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 20`() {
        fibonacciGenerator = FibonacciGenerator(20)
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `test isFibonacciNumber with a number in the sequence`() {
        fibonacciGenerator = FibonacciGenerator(100)
        assertTrue(fibonacciGenerator.isFibonacciNumber(0))
        assertTrue(fibonacciGenerator.isFibonacciNumber(1))
        assertTrue(fibonacciGenerator.isFibonacciNumber(5))
        assertTrue(fibonacciGenerator.isFibonacciNumber(13))
    }

    @Test
    fun `test isFibonacciNumber with a number not in the sequence`() {
        fibonacciGenerator = FibonacciGenerator(100)
        assertFalse(fibonacciGenerator.isFibonacciNumber(4))
        assertFalse(fibonacciGenerator.isFibonacciNumber(10))
        assertFalse(fibonacciGenerator.isFibonacciNumber(20))
    }

    @Test
    fun `test isFibonacciNumber with negative number`() {
        fibonacciGenerator = FibonacciGenerator(100)
        assertFalse(fibonacciGenerator.isFibonacciNumber(-1))
    }

    @Test
    fun `test edge case limit of 0`() {
        fibonacciGenerator = FibonacciGenerator(0)
        assertTrue(fibonacciGenerator.isFibonacciNumber(0))
        assertFalse(fibonacciGenerator.isFibonacciNumber(1))
    }

    @Test
    fun `test edge case limit of 1`() {
        fibonacciGenerator = FibonacciGenerator(1)
        assertTrue(fibonacciGenerator.isFibonacciNumber(0))
        assertTrue(fibonacciGenerator.isFibonacciNumber(1))
        assertFalse(fibonacciGenerator.isFibonacciNumber(2))
    }

    @Test
    fun `test edge case limit of 2`() {
        fibonacciGenerator = FibonacciGenerator(2)
        assertTrue(fibonacciGenerator.isFibonacciNumber(0))
        assertTrue(fibonacciGenerator.isFibonacciNumber(1))
        assertTrue(fibonacciGenerator.isFibonacciNumber(2))
    }
}
```

### Explanation:
1. **Tests for Sequence Generation**: It tests that the `FibonacciGenerator` correctly generates a Fibonacci sequence up to various limits (0, 1, 2, 10, 20) and verifies it against expected outputs.
2. **Tests for Checking Fibonacci Numbers**: It checks if specific numbers are in the generated Fibonacci sequence, both for numbers that are and are not in the sequence.
3. **Edge Cases**: Additional tests consider edge cases such as negative numbers and cases around the limit values (0, 1, and 2).

Make sure to include the necessary dependencies for JUnit in your project to run these tests.