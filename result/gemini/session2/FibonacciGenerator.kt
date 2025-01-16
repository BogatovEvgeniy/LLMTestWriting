Content(parts=[Part(text=```kotlin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FibonacciGeneratorTest {

    @Test
    fun `generateSequence generates correct sequence within limit`() {
        val generator = FibonacciGenerator(100)
        val expectedSequence = listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89)
        assertEquals(expectedSequence, generator.getSequence())
    }

    @Test
    fun `generateSequence handles limit of 0`() {
        val generator = FibonacciGenerator(0)
        assertEquals(listOf(0,1), generator.getSequence())
    }

    @Test
    fun `generateSequence handles limit of 1`() {
        val generator = FibonacciGenerator(1)
        assertEquals(listOf(0, 1), generator.getSequence())
    }

    @Test
    fun `generateSequence handles large limit`() {
        val generator = FibonacciGenerator(10000)
        assertTrue(generator.getSequence().size > 10) // Just a basic check for a large sequence
    }

    @Test
    fun `isFibonacciNumber returns true for numbers in the sequence`() {
        val generator = FibonacciGenerator(100)
        assertTrue(generator.isFibonacciNumber(0))
        assertTrue(generator.isFibonacciNumber(1))
        assertTrue(generator.isFibonacciNumber(55))
        assertTrue(generator.isFibonacciNumber(89))
    }

    @Test
    fun `isFibonacciNumber returns false for numbers not in the sequence`() {
        val generator = FibonacciGenerator(100)
        assertFalse(generator.isFibonacciNumber(4))
        assertFalse(generator.isFibonacciNumber(10))
        assertFalse(generator.isFibonacciNumber(1000))
    }

    @Test
    fun `isFibonacciNumber handles negative numbers`(){
        val generator = FibonacciGenerator(100)
        assertFalse(generator.isFibonacciNumber(-1))
    }

    @Test
    fun `getSequence returns immutable list`(){
        val generator = FibonacciGenerator(100)
        val sequence = generator.getSequence()
        assertThrows<UnsupportedOperationException> {
            sequence.add(100)
        }
    }


    @Test
    fun `emptyLimitTest`(){
        val generator = FibonacciGenerator(1)
        assertEquals(listOf(0,1), generator.getSequence())
    }

    @Test
    fun `largeLimitTest`(){
        val generator = FibonacciGenerator(100000)
        assertTrue(generator.getSequence().size > 100)
    }


}
```

This test suite covers various scenarios, including edge cases (limit of 0 and 1),  boundary conditions (large limits), and positive and negative test cases for `isFibonacciNumber`.  The addition of  `assertThrows` for immutability testing improves the robustness of the test suite. Remember to add JUnit Jupiter dependency to your project to run these tests.  For example, in Gradle:

```gradle
testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0-M1")
testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0-M1")
```

Remember to adjust the version numbers to match your project's dependencies.  You'll also need to adapt this if you're using a different testing framework.
)], role=model)