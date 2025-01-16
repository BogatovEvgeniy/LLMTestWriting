Content(parts=[Part(text=```kotlin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class ValidatedListTest {

    @Test
    fun `add valid element`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(true)
        val list = ValidatedList(validator)
        assertTrue(list.add(10))
        assertEquals(1, list.size)
        verify(validator).invoke(10)
    }

    @Test
    fun `add invalid element`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(false)
        val onValidationError = mock<(Int) -> Unit>()
        val list = ValidatedList(validator, onValidationError)
        assertFalse(list.add(10))
        assertEquals(0, list.size)
        verify(validator).invoke(10)
        verify(onValidationError).invoke(10)
    }

    @Test
    fun `addAt valid element`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(true)
        val list = ValidatedList(validator)
        list.add(0,10)
        assertEquals(1, list.size)
        assertEquals(10, list[0])
        verify(validator).invoke(10)
    }

    @Test
    fun `addAt invalid element`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(false)
        val onValidationError = mock<(Int) -> Unit>()
        val list = ValidatedList(validator, onValidationError)
        assertThrows<IllegalArgumentException> { list.add(0, 10) }
        assertEquals(0, list.size)
        verify(validator).invoke(10)
        verify(onValidationError).invoke(10)
    }

    @Test
    fun `addAll valid elements`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(true)
        `when`(validator(20)).thenReturn(true)
        val list = ValidatedList(validator)
        assertTrue(list.addAll(listOf(10, 20)))
        assertEquals(2, list.size)
        assertEquals(10, list[0])
        assertEquals(20, list[1])
        verify(validator).invoke(10)
        verify(validator).invoke(20)
    }

    @Test
    fun `addAll mixed elements`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(true)
        `when`(validator(20)).thenReturn(false)
        val onValidationError = mock<(Int) -> Unit>()
        val list = ValidatedList(validator, onValidationError)
        assertFalse(list.addAll(listOf(10, 20)))
        assertEquals(1, list.size)
        assertEquals(10, list[0])
        verify(validator).invoke(10)
        verify(validator).invoke(20)
        verify(onValidationError).invoke(20)
    }

    @Test
    fun `addAllAt mixed elements`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(true)
        `when`(validator(20)).thenReturn(false)
        val onValidationError = mock<(Int) -> Unit>()
        val list = ValidatedList(validator, onValidationError)
        assertFalse(list.addAll(0, listOf(10, 20)))
        assertEquals(1, list.size)
        assertEquals(10, list[0])
        verify(validator).invoke(10)
        verify(validator).invoke(20)
        verify(onValidationError).invoke(20)
    }

    @Test
    fun `set valid element`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(true)
        val list = ValidatedList(validator)
        list.add(20)
        assertEquals(20, list.set(0, 10))
        assertEquals(10, list[0])
        verify(validator).invoke(10)
    }

    @Test
    fun `set invalid element`() {
        val validator = mock<(Int) -> Boolean>()
        `when`(validator(10)).thenReturn(false)
        val onValidationError = mock<(Int) -> Unit>()
        val list = ValidatedList(validator, onValidationError)
        list.add(20)
        assertThrows<IllegalArgumentException> { list.set(0, 10) }
        assertEquals(20, list[0])
        verify(validator).invoke(10)
        verify(onValidationError).invoke(10)
    }


    @Test
    fun `testOtherMethods`(){
        val validator = {(i:Int) -> i > 0}
        val list = ValidatedList(validator)
        list.addAll(listOf(1,2,3))
        assertEquals(3, list.size)
        assertTrue(list.contains(2))
        assertEquals(2, list.indexOf(2))
        assertEquals(1, list.removeAt(0))
        assertEquals(2, list.size)
        list.clear()
        assertTrue(list.isEmpty())

    }
}
```)], role=model)