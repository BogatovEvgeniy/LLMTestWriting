Certainly! Here’s a set of unit tests for the `FibonacciGenerator` class. These tests will cover the construction of the Fibonacci sequence, retrieving the sequence, and checking if a number is part of the sequence.

Assuming we're using a testing framework like JUnit for Kotlin, here’s how you might write the tests:

```kotlin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {

    @Test
    fun `test sequence generation up to limit 0`() {
        val generator = FibonacciGenerator(0)
        assertEquals(listOf(0), generator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 1`() {
        val generator = FibonacciGenerator(1)
        assertEquals(listOf(0, 1), generator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 2`() {
        val generator = FibonacciGenerator(2)
        assertEquals(listOf(0, 1, 1, 2), generator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 5`() {
        val generator = FibonacciGenerator(5)
        assertEquals(listOf(0, 1, 1, 2, 3, 5), generator.getSequence())
    }

    @Test
    fun `test sequence generation up to limit 10`() {
        val generator = FibonacciGenerator(10)
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8), generator.getSequence())
    }

    @Test
    fun `test checking if a number is a Fibonacci number - true cases`() {
        val generator = FibonacciGenerator(20)
        assertTrue(generator.isFibonacciNumber(0))
        assertTrue(generator.isFibonacciNumber(1))
        assertTrue(generator.isFibonacciNumber(2))
        assertTrue(generator.isFibonacciNumber(3))
        assertTrue(generator.isFibonacciNumber(5))
        assertTrue(generator.isFibonacciNumber(8))
    }

    @Test
    fun `test checking if a number is a Fibonacci number - false cases`() {
        val generator = FibonacciGenerator(20)
        assertFalse(generator.isFibonacciNumber(4))
        assertFalse(generator.isFibonacciNumber(6))
        assertFalse(generator.isFibonacciNumber(7))
        assertFalse(generator.isFibonacciNumber(9))
        assertFalse(generator.isFibonacciNumber(10))
    }

    @Test
    fun `test checking if a number is a Fibonacci number - large numbers`() {
        val generator = FibonacciGenerator(1000000)
        assertTrue(generator.isFibonacciNumber(144))
        assertTrue(generator.isFibonacciNumber(233))
        assertFalse(generator.isFibonacciNumber(145))
    }

    @Test
    fun `test edge case for negative numbers`() {
        val generator = FibonacciGenerator(0)
        assertFalse(generator.isFibonacciNumber(-1))
        assertFalse(generator.isFibonacciNumber(-10))
    }
}
```

### Explanation of the Tests:
- **Sequence Generation Tests**: 
  - Check that the generated sequence includes the correct Fibonacci numbers up to various limits.
  
- **Fibonacci Number Checks**: 
  - Test both positive cases (where the number is in the Fibonacci sequence) and negative cases (not in the sequence).
  
- **Edge Case Tests**: 
  - Check for behavior with negative numbers, which should return `false` as they cannot be Fibonacci numbers.

You should run these tests in a suitable environment where you have the `FibonacciGenerator` class defined, and ensure that the testing framework (JUnit in this case) is properly set up for your project.