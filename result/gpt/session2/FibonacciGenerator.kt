To create unit tests for the `FibonacciGenerator` class, we would typically use a testing framework such as JUnit in Kotlin. Below is an example set of unit tests that you can use to verify the functionality of this class:

```kotlin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {

    private lateinit var fibonacciGenerator: FibonacciGenerator

    @BeforeEach
    fun setUp() {
        // Initialize the FibonacciGenerator with a limit
        fibonacciGenerator = FibonacciGenerator(21)
    }

    @Test
    fun `should generate correct Fibonacci sequence up to a limit`() {
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `should return true for Fibonacci numbers`() {
        assertTrue(fibonacciGenerator.isFibonacciNumber(0))
        assertTrue(fibonacciGenerator.isFibonacciNumber(1))
        assertTrue(fibonacciGenerator.isFibonacciNumber(2))
        assertTrue(fibonacciGenerator.isFibonacciNumber(3))
        assertTrue(fibonacciGenerator.isFibonacciNumber(5))
        assertTrue(fibonacciGenerator.isFibonacciNumber(21))
    }

    @Test
    fun `should return false for non-Fibonacci numbers`() {
        assertFalse(fibonacciGenerator.isFibonacciNumber(4))
        assertFalse(fibonacciGenerator.isFibonacciNumber(6))
        assertFalse(fibonacciGenerator.isFibonacciNumber(7))
        assertFalse(fibonacciGenerator.isFibonacciNumber(9))
        assertFalse(fibonacciGenerator.isFibonacciNumber(22))
    }

    @Test
    fun `should generate an empty sequence when limit is less than 0`() {
        val emptyGenerator = FibonacciGenerator(-1)
        assertTrue(emptyGenerator.getSequence().isEmpty())
    }

    @Test
    fun `should only include zero and one when limit is 1`() {
        val limitOneGenerator = FibonacciGenerator(1)
        val expectedSequence = listOf(0, 1)
        assertEquals(expectedSequence, limitOneGenerator.getSequence())
    }
}
```

### Explanation of the Test Cases

1. **`setUp` Method**: This method initializes a `FibonacciGenerator` object before each test case, which makes it easier to manage test states.

2. **`should generate correct Fibonacci sequence up to a limit`**: This test checks that the Fibonacci sequence generated up to the specified limit matches the expected result.

3. **`should return true for Fibonacci numbers`**: This test verifies that the `isFibonacciNumber` method returns `true` for known Fibonacci numbers within the limit.

4. **`should return false for non-Fibonacci numbers`**: This test ensures that the `isFibonacciNumber` method returns `false` for numbers that are not part of the Fibonacci sequence.

5. **`should generate an empty sequence when limit is less than 0`**: This test checks the behavior of the generator when initialized with a negative limit, confirming that it generates an empty sequence.

6. **`should only include zero and one when limit is 1`**: This test verifies that when the limit is set to 1, the sequence only contains the initial two Fibonacci numbers.

### Running the Tests
You can run these tests using a test runner compatible with JUnit in your Kotlin development environment. Make sure that your project includes the necessary dependencies for JUnit.

### Additional Considerations
- You might want to add more tests for edge cases or consider returning more informative results (like throwing exceptions) in cases where an invalid limit is set (e.g., negative numbers).