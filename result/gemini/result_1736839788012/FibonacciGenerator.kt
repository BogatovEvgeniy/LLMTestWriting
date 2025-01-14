To create unit tests for the `FibonacciGenerator` class, we can use a testing framework like JUnit in Kotlin. Below is a set of unit tests that will cover the main functionalities of your class, including generating the Fibonacci sequence up to a specified limit and checking if a number is part of that sequence.

First, ensure you have the necessary dependencies for JUnit in your `build.gradle.kts`:

```kotlin
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}
```

Now, here are the unit tests:

```kotlin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FibonacciGeneratorTest {

    @Test
    fun `test when limit is 0, sequence should contain only 0`() {
        val generator = FibonacciGenerator(0)
        assertEquals(listOf(0), generator.getSequence())
    }

    @Test
    fun `test when limit is 1, sequence should contain 0 and 1`() {
        val generator = FibonacciGenerator(1)
        assertEquals(listOf(0, 1), generator.getSequence())
    }

    @Test
    fun `test when limit is 2, sequence should contain 0, 1, and 1`() {
        val generator = FibonacciGenerator(2)
        assertEquals(listOf(0, 1, 1), generator.getSequence())
    }

    @Test
    fun `test when limit is 5, sequence should contain 0, 1, 1, 2, 3, and 5`() {
        val generator = FibonacciGenerator(5)
        assertEquals(listOf(0, 1, 1, 2, 3, 5), generator.getSequence())
    }

    @Test
    fun `test when limit is 21, sequence should contain all Fibonacci numbers below or equal to 21`() {
        val generator = FibonacciGenerator(21)
        assertEquals(listOf(0, 1, 1, 2, 3, 5, 8, 13, 21), generator.getSequence())
    }

    @Test
    fun `test isFibonacciNumber returns true for a Fibonacci number`() {
        val generator = FibonacciGenerator(10)
        assertTrue(generator.isFibonacciNumber(0))
        assertTrue(generator.isFibonacciNumber(1))
        assertTrue(generator.isFibonacciNumber(2))
        assertTrue(generator.isFibonacciNumber(3))
        assertTrue(generator.isFibonacciNumber(5))
    }

    @Test
    fun `test isFibonacciNumber returns false for a non-Fibonacci number`() {
        val generator = FibonacciGenerator(10)
        assertFalse(generator.isFibonacciNumber(4))
        assertFalse(generator.isFibonacciNumber(6))
        assertFalse(generator.isFibonacciNumber(7))
        assertFalse(generator.isFibonacciNumber(9))
        assertFalse(generator.isFibonacciNumber(11))
    }

    @Test
    fun `test isFibonacciNumber returns false for negative numbers`() {
        val generator = FibonacciGenerator(10)
        assertFalse(generator.isFibonacciNumber(-1))
        assertFalse(generator.isFibonacciNumber(-10))
    }

    @Test
    fun `test limit scenario, the sequence should not exceed the limit - 1`() {
        val generator = FibonacciGenerator(1_000)
        val lastItem = generator.getSequence().last()
        assertTrue(lastItem <= 1_000)
    }
}
```

### Explanation of the Tests

1. **Basic Sequence Generation Tests**: We test that for various limits (0, 1, 2, 5, and 21), the generated Fibonacci sequence is correct.

2. **Fibonacci Number Check Tests**: We verify that the function `isFibonacciNumber` correctly identifies Fibonacci numbers and returns `true` for those, while returning `false` for non-Fibonacci numbers and any negative numbers.

3. **Limit Test**: We ensure that the generated Fibonacci sequence does not exceed the limit passed to the constructor by checking the last item in the generated sequence.

Make sure to run the tests in your development environment. Adjust the package imports as necessary to fit your project structure.