Certainly! Below is a set of unit tests for the `FibonacciGenerator` class using Kotlin and the `JUnit` testing framework. These tests will verify the behavior of the Fibonacci sequence generation and the method that checks for Fibonacci numbers.

```kotlin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {

    private lateinit var fibonacciGenerator: FibonacciGenerator

    @BeforeEach
    fun setUp() {
        fibonacciGenerator = FibonacciGenerator(21) // Adjust the limit as necessary for testing
    }

    @Test
    fun `should generate Fibonacci sequence up to limit`() {
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `should return true for Fibonacci numbers within limit`() {
        val fibonacciNumbers = listOf(0, 1, 2, 3, 5, 8, 13, 21)

        for (number in fibonacciNumbers) {
            assertTrue(fibonacciGenerator.isFibonacciNumber(number), "$number should be a Fibonacci number")
        }
    }

    @Test
    fun `should return false for non-Fibonacci numbers within limit`() {
        val nonFibonacciNumbers = listOf(4, 6, 7, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20)

        for (number in nonFibonacciNumbers) {
            assertFalse(fibonacciGenerator.isFibonacciNumber(number), "$number should NOT be a Fibonacci number")
        }
    }

    @Test
    fun `should return false for numbers greater than limit`() {
        // Since we set limit to 21, testing for 22, 23, etc.
        val numberGreaterThanLimit = 22
        assertFalse(fibonacciGenerator.isFibonacciNumber(numberGreaterThanLimit), "$numberGreaterThanLimit should NOT be a Fibonacci number")
    }

    @Test
    fun `should generate an empty sequence for limit less than 0`() {
        val generatorNegativeLimit = FibonacciGenerator(-1)
        assertTrue(generatorNegativeLimit.getSequence().isEmpty(), "Sequence should be empty for negative limit")
    }

    @Test
    fun `should generate only 0 and 1 for limit equal to 1`() {
        val generatorLimitOne = FibonacciGenerator(1)
        val expectedSequence = listOf(0, 1)
        assertEquals(expectedSequence, generatorLimitOne.getSequence())
    }

    @Test
    fun `should generate correct sequence for higher limits`() {
        fibonacciGenerator = FibonacciGenerator(34)
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }
}
```

### Notes

- In the above tests:
  - `setUp()` initializes an instance of `FibonacciGenerator` before each test.
  - Various scenarios are tested, including the generation of the Fibonacci sequence up to a given limit, checking for Fibonacci numbers, handling limits that result in no Fibonacci numbers, and edge cases like negative limits and a limit of 1.
  - Ensure you have the necessary dependencies for JUnit in your project to run these tests.

To run these tests, you would typically use an IDE like IntelliJ IDEA which supports Kotlin, or you can run them using Gradle or Maven if your project is set up with those tools.