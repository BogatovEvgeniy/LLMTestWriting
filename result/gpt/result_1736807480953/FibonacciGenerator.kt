To effectively test the `FibonacciGenerator` class, we need to cover various scenarios including valid limits, edge cases, and functionality checks for both generating the sequence and checking if a number is part of the Fibonacci sequence. Below are a set of unit tests using a Kotlin testing framework such as JUnit.

```kotlin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {

    @Test
    @DisplayName("Test Fibonacci sequence generation with a limit of 0")
    fun testFibonacciSequenceWithZeroLimit() {
        val generator = FibonacciGenerator(0)
        val expectedSequence = listOf(0)
        assertEquals(expectedSequence, generator.getSequence())
    }

    @Test
    @DisplayName("Test Fibonacci sequence generation with a limit of 1")
    fun testFibonacciSequenceWithOneLimit() {
        val generator = FibonacciGenerator(1)
        val expectedSequence = listOf(0, 1)
        assertEquals(expectedSequence, generator.getSequence())
    }

    @Test
    @DisplayName("Test Fibonacci sequence generation with a limit of 2")
    fun testFibonacciSequenceWithTwoLimit() {
        val generator = FibonacciGenerator(2)
        val expectedSequence = listOf(0, 1, 1, 2)
        assertEquals(expectedSequence, generator.getSequence())
    }

    @Test
    @DisplayName("Test Fibonacci sequence generation with a limit of 21")
    fun testFibonacciSequenceWithTwentyOneLimit() {
        val generator = FibonacciGenerator(21)
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21)
        assertEquals(expectedSequence, generator.getSequence())
    }

    @Test
    @DisplayName("Test Fibonacci sequence generation with a high limit of 100")
    fun testFibonacciSequenceWithOneHundredLimit() {
        val generator = FibonacciGenerator(100)
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89)
        assertEquals(expectedSequence, generator.getSequence())
    }

    @Test
    @DisplayName("Test if a number is a Fibonacci number (True case)")
    fun testIsFibonacciNumberTrue() {
        val generator = FibonacciGenerator(21)
        assertTrue(generator.isFibonacciNumber(0))
        assertTrue(generator.isFibonacciNumber(1))
        assertTrue(generator.isFibonacciNumber(2))
        assertTrue(generator.isFibonacciNumber(3))
        assertTrue(generator.isFibonacciNumber(5))
        assertTrue(generator.isFibonacciNumber(13))
        assertTrue(generator.isFibonacciNumber(21))
    }

    @Test
    @DisplayName("Test if a number is a Fibonacci number (False case)")
    fun testIsFibonacciNumberFalse() {
        val generator = FibonacciGenerator(21)
        assertFalse(generator.isFibonacciNumber(4))
        assertFalse(generator.isFibonacciNumber(6))
        assertFalse(generator.isFibonacciNumber(7))
        assertFalse(generator.isFibonacciNumber(10))
        assertFalse(generator.isFibonacciNumber(22))
    }

    @Test
    @DisplayName("Test Fibonacci number check for a limit greater than required Fibonacci number")
    fun testCheckFibonacciWithinLimit() {
        val generator = FibonacciGenerator(50)
        assertTrue(generator.isFibonacciNumber(34))
        assertFalse(generator.isFibonacciNumber(40))
        
        // Check boundary conditions
        assertTrue(generator.isFibonacciNumber(0))
        assertTrue(generator.isFibonacciNumber(1))
        assertTrue(generator.isFibonacciNumber(2))
        assertTrue(generator.isFibonacciNumber(3))
    }

}
```

### Explanation of the Tests:

1. **Boundary Conditions**: Tests for zero and one, as these are special cases in the Fibonacci sequence.
  
2. **Standard Cases**: Tests with small, common limits (2, 21, 100) to assert that the sequence is generated correctly.

3. **True and False Checks**: Validate `isFibonacciNumber()` for numbers known to be in the Fibonacci sequence and those that are not.

4. **Functionality Validation**: Ensure that the sequence generation is accurate for all inputs and that the checks for Fibonacci numbers return the correct results.

### Running Tests

These tests should be run within a testing framework compatible with Kotlin. You can use Gradle or Maven along with JUnit 5 to manage your tests effectively.