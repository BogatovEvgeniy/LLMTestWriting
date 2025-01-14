Sure! Below is a set of unit tests for the `FibonacciGenerator` class you provided. These tests verify the behavior of the constructor, the `getSequence` method, and the `isFibonacciNumber` method.

You can utilize a testing framework like JUnit for these tests. Here’s an example implementation in Kotlin using JUnit 5.

### Unit Tests for `FibonacciGenerator`

```kotlin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {

    private lateinit var fibonacciGenerator: FibonacciGenerator

    @BeforeEach
    fun setUp() {
        // You can change this limit for further test cases
        fibonacciGenerator = FibonacciGenerator(21) // Test with limit 21
    }

    @Test
    fun `test fibonacci sequence generation up to limit`() {
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21)
        assertEquals(expectedSequence, fibonacciGenerator.getSequence())
    }

    @Test
    fun `test fibonacci sequence not exceeding limit`() {
        val limitFibonacci = 8
        val generator = FibonacciGenerator(limitFibonacci)
        assertTrue(generator.getSequence().all { it <= limitFibonacci })
    }

    @Test
    fun `test isFibonacciNumber true for fibonacci numbers`() {
        val fibonacciNumbers = listOf(0, 1, 2, 3, 5, 8, 13, 21)
        for (number in fibonacciNumbers) {
            assertTrue(fibonacciGenerator.isFibonacciNumber(number), "$number should be a Fibonacci number")
        }
    }

    @Test
    fun `test isFibonacciNumber false for non-fibonacci numbers`() {
        val nonFibonacciNumbers = listOf(4, 6, 7, 9, 10, 12, 14, 15, 16, 17, 18, 19, 20)
        for (number in nonFibonacciNumbers) {
            assertFalse(fibonacciGenerator.isFibonacciNumber(number), "$number should not be a Fibonacci number")
        }
    }

    @Test
    fun `test empty sequence for limit zero`() {
        val generator = FibonacciGenerator(0)
        assertEquals(listOf(0), generator.getSequence(), "Sequence should only contain 0 when limit is 0")
    }

    @Test
    fun `test sequence generation for a negative limit`() {
        val generator = FibonacciGenerator(-1)
        assertEquals(listOf(0), generator.getSequence(), "Sequence should only contain 0 when limit is negative")
    }
}
```

### Explanation of the Tests

1. **test fibonacci sequence generation up to limit**: This test checks if the Fibonacci sequence generated up to a given limit is correct.

2. **test fibonacci sequence not exceeding limit**: This test ensures that all numbers in the generated Fibonacci sequence do not exceed the specified limit.

3. **test isFibonacciNumber true for fibonacci numbers**: This test verifies that known Fibonacci numbers are correctly recognized as being part of the sequence.

4. **test isFibonacciNumber false for non-fibonacci numbers**: This test checks that non-Fibonacci numbers are not mistakenly identified as Fibonacci numbers.

5. **test empty sequence for limit zero**: This test checks that when the limit is zero, only `0` is part of the sequence.

6. **test sequence generation for a negative limit**: This test checks that when the limit is negative, again only `0` is part of the sequence.

### Running the Tests

To run these tests, include JUnit 5 in your build system (e.g., Gradle or Maven) dependencies. If you're using an IDE like IntelliJ IDEA, it will automatically recognize these tests, and you can run them directly from there.