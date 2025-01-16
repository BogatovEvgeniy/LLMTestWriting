Content(parts=[Part(text=```kotlin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidatedListTest {

    @Test
    fun `add valid element`() {
        val validator: (Int) -> Boolean = { it > 0 }
        val list = ValidatedList(validator)
        assertTrue(list.add(5))
        assertEquals(1, list.size)
        assertEquals(5, list[0])
    }

    @Test
    fun `add invalid element`() {
        var invalidElement = 0
        val validator: (Int) -> Boolean = { it > 0 }
        val onValidationError: (Int) -> Unit = { invalidElement = it }
        val list = ValidatedList(validator, onValidationError)
        assertFalse(list.add(-5))
        assertEquals(0, list.size)
        assertEquals(-5, invalidElement)
    }

    @Test
    fun `add at index valid element`() {
        val validator: (Int) -> Boolean = { it > 0 }
        val list = ValidatedList(validator)
        list.add(5)
        list.add(1, 10)
        assertEquals(2, list.size)
        assertEquals(10, list[0])
        assertEquals(5, list[1])
    }

    @Test
    fun `add at index invalid element`() {
        var invalidElement = 0
        val validator: (Int) -> Boolean = { it > 0 }
        val onValidationError: (Int) -> Unit = { invalidElement = it }
        val list = ValidatedList(validator, onValidationError)
        list.add(5)
        assertThrows<IllegalArgumentException> { list.add(1, -10) }
        assertEquals(1, list.size)
        assertEquals(-10, invalidElement)
    }

    @Test
    fun `addAll valid elements`() {
        val validator: (Int) -> Boolean = { it > 0 }
        val list = ValidatedList(validator)
        assertTrue(list.addAll(listOf(5, 10, 15)))
        assertEquals(3, list.size)
        assertEquals(listOf(5, 10, 15), list)
    }

    @Test
    fun `addAll mixed elements`() {
        var invalidElements = emptyList<Int>()
        val validator: (Int) -> Boolean = { it > 0 }
        val onValidationError: (Int) -> Unit = { invalidElements = invalidElements + it }
        val list = ValidatedList(validator, onValidationError)
        assertTrue(list.addAll(listOf(5, -10, 15, -20)))
        assertEquals(2, list.size)
        assertEquals(listOf(5, 15), list)
        assertEquals(listOf(-10, -20), invalidElements)
    }

    @Test
    fun `addAll at index valid elements`() {
        val validator: (Int) -> Boolean = { it > 0 }
        val list = ValidatedList(validator)
        list.add(10)
        assertTrue(list.addAll(0, listOf(5, 15)))
        assertEquals(3, list.size)
        assertEquals(listOf(5, 15, 10), list)
    }

    @Test
    fun `addAll at index mixed elements`() {
        var invalidElements = emptyList<Int>()
        val validator: (Int) -> Boolean = { it > 0 }
        val onValidationError: (Int) -> Unit = { invalidElements = invalidElements + it }
        val list = ValidatedList(validator, onValidationError)
        list.add(10)
        list.addAll(0, listOf(5, -15, 20))
        assertEquals(3, list.size)
        assertEquals(listOf(5, 20, 10), list)
        assertEquals(listOf(-15), invalidElements)
    }

    @Test
    fun `set valid element`() {
        val validator: (Int) -> Boolean = { it > 0 }
        val list = ValidatedList(validator)
        list.add(5)
        assertEquals(5, list.set(0, 10))
        assertEquals(10, list[0])
    }

    @Test
    fun `set invalid element`() {
        var invalidElement = 0
        val validator: (Int) -> Boolean = { it > 0 }
        val onValidationError: (Int) -> Unit = { invalidElement = it }
        val list = ValidatedList(validator, onValidationError)
        list.add(5)
        assertThrows<IllegalArgumentException> { list.set(0, -10) }
        assertEquals(5, list[0])
        assertEquals(-10, invalidElement)
    }


    // Add more tests for other MutableList methods as needed...  (removeAt, subList, etc.)
}
```

This provides a good starting point.  Remember to add more tests to cover the remaining `MutableList` methods (`removeAt`, `subList`, `retainAll`, `removeAll`, `remove`, `clear`,  iterator functions, etc.) to ensure complete test coverage.  Consider edge cases like empty lists, lists with only invalid elements, and boundary conditions for indices.  You might also want to test with different validator functions (e.g., string validators, custom object validators).
)], role=model)