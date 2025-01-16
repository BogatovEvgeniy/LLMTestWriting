To create a set of unit tests for the `DataProcessor` class provided, we'll use a testing framework like JUnit along with a mocking library like Mockito or MockK (for Kotlin). The goal of the tests is to verify that the `processData` function behaves correctly under different scenarios.

Here's a complete set of unit tests covering various cases:

```kotlin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class DataProcessorTest {

    private val dataValidator: DataValidator = mock(DataValidator::class.java)
    private val dataTransformer: DataTransformer = mock(DataTransformer::class.java)
    private val dataRepository: DataRepository = mock(DataRepository::class.java)
    private val dataProcessor = DataProcessor(dataValidator, dataTransformer, dataRepository)

    @Test
    fun `processData should return Empty when input is empty`() = runBlocking {
        val result = dataProcessor.processData(emptyList())
        assertTrue(result is ProcessingResult.Empty)
    }

    @Test
    fun `processData should return Empty when all data is invalid`() = runBlocking {
        val rawDataList = listOf(RawData(), RawData())
        `when`(dataValidator.validate(any())).thenReturn(false)

        val result = dataProcessor.processData(rawDataList)
        assertTrue(result is ProcessingResult.Empty)
    }

    @Test
    fun `processData should return Success when all data is valid and saved`() = runBlocking {
        val rawDataList = listOf(RawData(), RawData())
        val transformedDataList = listOf(TransformedData(false), TransformedData(false))

        // Setting up validator and transformer mocks.
        `when`(dataValidator.validate(any())).thenReturn(true)
        `when`(dataTransformer.transform(any())).thenAnswer { transformedDataList[it.arguments[0] as Int] }
        
        val result = dataProcessor.processData(rawDataList)

        assertTrue(result is ProcessingResult.Success)
        assertEquals(transformedDataList, (result as ProcessingResult.Success).data)
        verify(dataRepository).saveAll(transformedDataList)
    }

    @Test
    fun `processData should return PartialSuccess when some data is valid and some are invalid`() = runBlocking {
        val rawDataList = listOf(RawData(), RawData(), RawData())
        val transformedDataList = listOf(TransformedData(false), TransformedData(true), TransformedData(false))

        `when`(dataValidator.validate(any())).thenReturn(true, false, true)
        `when`(dataTransformer.transform(any())).thenAnswer { transformedDataList[it.arguments[0] as Int] }

        val result = dataProcessor.processData(rawDataList)

        assertTrue(result is ProcessingResult.PartialSuccess)
        result as ProcessingResult.PartialSuccess
        assertEquals(2, result.successful.size)
        assertEquals(1, result.failed.size)
    }

    @Test
    fun `processData should return Error when an exception occurs`() = runBlocking {
        val rawDataList = listOf(RawData())
        `when`(dataValidator.validate(any())).thenThrow(RuntimeException("Error"))

        val result = dataProcessor.processData(rawDataList)

        assertTrue(result is ProcessingResult.Error)
        assertEquals("Error", (result as ProcessingResult.Error).exception.message)
    }
}
```

### Explanation of Tests:
1. **Empty Input Test**: Checks if `processData` correctly handles an empty list and returns `ProcessingResult.Empty`.
  
2. **All Invalid Data Test**: Ensures that if all data is invalid, `processData` returns `ProcessingResult.Empty`.

3. **All Valid Data Test**: Verifies that when all raw data is valid, it is transformed and saved correctly, resulting in a `ProcessingResult.Success`.

4. **Partial Success Test**: Tests for a scenario where some data are valid and others are not, returning a `ProcessingResult.PartialSuccess` with the successful and failed data.

5. **Exception Test**: Asserts that if an exception is thrown during processing, it results in a `ProcessingResult.Error`.

### Note: 
- This example uses Mockito's `when` and `thenReturn` constructs to mock the behavior of the dependencies. Adjust the imports and mocking framework usage according to your project's setup.
- It uses Kotlin coroutines, and therefore `runBlocking` is used to maintain the coroutine context during the tests.
- The data classes such as `RawData`, `TransformedData`, and the validator, transformer, and repository interfaces/classes need to be defined for this test code to run successfully.
- The use of `async` and `awaitAll` suggests that multiple transformations take place