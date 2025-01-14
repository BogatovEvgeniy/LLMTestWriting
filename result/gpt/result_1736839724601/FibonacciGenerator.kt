Here is a set of unit tests for the `FibonacciGenerator` class using Kotlin and the JUnit testing framework. These tests will verify the functionality of generating the Fibonacci sequence up to a specified limit, returning the sequence correctly, and checking if a number is a Fibonacci number.

First, make sure you have the following dependencies in your `build.gradle` file for Kotlin and JUnit:

```groovy
dependencies {
    testImplementation 'org.jetbrains.kotlin:kotlin-test-junit'
}
```

Now, here is the test code:

```kotlin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {
    
    @Test
    fun `test Fibonacci sequence generation up to limit 0`() {
        val generator = FibonacciGenerator(0)
        assertEquals(listOf(0, 1), generator.getSequence())
    }

    @Test
    fun `test Fibonacci sequence generation up to limit 1`() {
        val generator = FibonacciGenerator(1)
        assertEquals(listOf(0, 1), generator.getSequence())
    }

    @Test
    fun `test Fibonacci sequence generation up to limit 5`() {
        val generator = FibonacciGenerator(5)
        assertEquals(listOf(0, 1, 1, 2, 3, 5), generator.getSequence())
    }

    @Test
    fun `test Fibonacci sequence generation up to limit 10`() {
        val generator = FibonacciGenerator(10)
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8), generator.getSequence())
    }

    @Test
    fun `test Fibonacci sequence generation with a large limit`() {
        val generator = FibonacciGenerator(100)
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89), generator.getSequence())
    }

    @Test
    fun `test if a number is Fibonacci - true cases`() {
        val generator = FibonacciGenerator(100)
        assertTrue(generator.isFibonacciNumber(0))
        assertTrue(generator.isFibonacciNumber(1))
        assertTrue(generator.isFibonacciNumber(2))
        assertTrue(generator.isFibonacciNumber(3))
        assertTrue(generator.isFibonacciNumber(5))
        assertTrue(generator.isFibonacciNumber(8))
        assertTrue(generator.isFibonacciNumber(13))
        assertTrue(generator.isFibonacciNumber(21))
        assertTrue(generator.isFibonacciNumber(34))
        assertTrue(generator.isFibonacciNumber(55))
        assertTrue(generator.isFibonacciNumber(89))
    }

    @Test
    fun `test if a number is Fibonacci - false cases`() {
        val generator = FibonacciGenerator(100)
        assertFalse(generator.isFibonacciNumber(4))
        assertFalse(generator.isFibonacciNumber(6))
        assertFalse(generator.isFibonacciNumber(7))
        assertFalse(generator.isFibonacciNumber(9))
        assertFalse(generator.isFibonacciNumber(10))
        assertFalse(generator.isFibonacciNumber(30))
    }

    @Test
    fun `test if a negative number is Fibonacci - should be false`() {
        val generator = FibonacciGenerator(100)
        assertFalse(generator.isFibonacciNumber(-1))
        assertFalse(generator.isFibonacciNumber(-5))
    }
}
```

### Explanation of Tests:
1. **Generation Tests**: Tests are provided for various limits (0, 1, 5, 10, and 100) to confirm that the Fibonacci sequence generation occurs correctly.
2. **Fibonacci Number Check (True)**: Tests to check if several known Fibonacci numbers return `true`.
3. **Fibonacci Number Check (False)**: Tests to ensure that non-Fibonacci numbers return `false`.
4. **Negative Numbers**: Test to confirm that negative integers are correctly identified as not Fibonacci numbers.

### Running the Tests
You can run these tests using your IDE or through the command line using Gradle. Make sure your class file and test file are in the correct directory structure (`src/test/kotlin`).