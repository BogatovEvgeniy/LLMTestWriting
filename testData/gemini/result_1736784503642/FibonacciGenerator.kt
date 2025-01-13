To create unit tests for the `FibonacciGenerator` class provided, we'll use a testing framework like JUnit. The tests will cover the basic functionality of the class. Here's a sample of how you could structure the unit tests:

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
        // You can change the limit for different test cases
        fibonacciGenerator = FibonacciGenerator(100)
    }

    @Test
    fun testFibonacciSequence() {
        // Validating if the sequence generated up to the limit is correct
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun testSequenceLimit() {
        // Testing the limit scenario
        val generator = FibonacciGenerator(10)
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8)
        assertEquals(expectedSequence, generator.getSequence())
    }

    @Test
    fun testIsFibonacciNumber() {
        // Testing if specific numbers are part of the Fibonacci sequence
        assertTrue(fibonacciGenerator.isFibonacciNumber(0))
        assertTrue(fibonacciGenerator.isFibonacciNumber(21))
        assertTrue(fibonacciGenerator.isFibonacciNumber(34))
        assertFalse(fibonacciGenerator.isFibonacciNumber(4))
        assertFalse(fibonacciGenerator.isFibonacciNumber(22))
    }

    @Test
    fun testIsFibonacciNumberWithNegativeInput() {
        // Testing that negative numbers are not part of the Fibonacci sequence
        assertFalse(fibonacciGenerator.isFibonacciNumber(-1))
        assertFalse(fibonacciGenerator.isFibonacciNumber(-5))
    }

    @Test
    fun testFibonacciSequenceEmptyForZeroLimit() {
        // Testing the case when the limit is 0
        val generator = FibonacciGenerator(0)
        val expectedSequence = listOf(0)
        assertEquals(expectedSequence, generator.getSequence())
    }
}
```

### Explanation of the Test Cases:

1. **`testFibonacciSequence()`**: This test checks that the Fibonacci sequence is generated correctly up to the limit of 100.

2. **`testSequenceLimit()`**: This test checks the Fibonacci sequence when the limit is set to 10. It validates that the sequence includes all Fibonacci numbers up to this limit.

3. **`testIsFibonacciNumber()`**: This test checks various positive numbers, asserting which are Fibonacci numbers and which are not.

4. **`testIsFibonacciNumberWithNegativeInput()`**: This test checks that negative numbers return false for being Fibonacci numbers, which aligns with the expected results defined by the Fibonacci sequence.

5. **`testFibonacciSequenceEmptyForZeroLimit()`**: This checks behavior when the limit is 0, expecting the sequence to only include 0.

### Instructions for Running Tests:

Make sure you have a suitable testing environment set up with JUnit. You can run these tests using an IDE such as IntelliJ IDEA, or through the command line using Gradle or Maven commands if your project is set up accordingly.