To write unit tests for the `DataProcessor` class in Kotlin, you'll typically want to use a testing framework such as JUnit, and for coroutines, `kotlinx.coroutines.test` can be quite helpful. Below is a set of unit tests covering different scenarios that can occur with the `processData` function. For the purpose of these tests, mock implementations of `DataValidator`, `DataTransformer`, and `DataRepository` are used.

Let's assume you have the following dependencies:

```kotlin
dependencies {
    testImplementation "org.junit.jupiter:junit-jupiter-api:5.7.0"
    testImplementation "org.junit.jupiter:junit-jupiter-engine:5.7.0"
    testImplementation "io.mockk:mockk:1.10.0"
    testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0"
}
```

Here is how you might set up your unit tests:

```kotlin
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DataProcessorTest {
    private lateinit var dataValidator: DataValidator
    private lateinit var dataTransformer: DataTransformer
    private lateinit var dataRepository: DataRepository
    private lateinit var dataProcessor: DataProcessor

    @BeforeEach
    fun setUp() {
        dataValidator = mockk()
        dataTransformer = mockk()
        dataRepository = mockk()
        dataProcessor = DataProcessor(dataValidator, dataTransformer, dataRepository)

        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `processData returns Success when all data is valid and transforms successfully`() = runBlocking {
        val rawDataList = listOf(RawData(), RawData())
        val transformedDataList = listOf(TransformedData(), TransformedData())
        
        // Mocking
        every { dataValidator.validate(any()) } returns true
        every { dataTransformer.transform(any()) } returns transformedDataList[0] andThen transformedDataList[1]
        coEvery { dataRepository.saveAll(any()) } just Runs

        // Act
        val result = dataProcessor.processData(rawDataList)

        // Assert
        assertTrue(result is ProcessingResult.Success)
        assertEquals(transformedDataList, (result as ProcessingResult.Success).data)
        coVerify { dataRepository.saveAll(transformedDataList) }
    }

    @Test
    fun `processData returns PartialSuccess when some data fails transformation`() = runBlocking {
        val rawDataList = listOf(RawData(), RawData())
        val transformedDataList = listOf(TransformedData(isError = true), TransformedData())

        // Mocking
        every { dataValidator.validate(any()) } returns true
        every { dataTransformer.transform(any()) } returns transformedDataList[0] andThen transformedDataList[1]

        // Act
        val result = dataProcessor.processData(rawDataList)

        // Assert
        assertTrue(result is ProcessingResult.PartialSuccess)
        val partialSuccess = result as ProcessingResult.PartialSuccess
        assertEquals(1, partialSuccess.successful.size)
        assertEquals(1, partialSuccess.failed.size)
        assertEquals(transformedDataList[1], partialSuccess.successful[0])
        assertEquals(transformedDataList[0], partialSuccess.failed[0])
    }

    @Test
    fun `processData returns Empty when no data is valid`() = runBlocking {
        val rawDataList = listOf(RawData(), RawData())

        // Mocking
        every { dataValidator.validate(any()) } returns false

        // Act
        val result = dataProcessor.processData(rawDataList)

        // Assert
        assertTrue(result is ProcessingResult.Empty)
    }

    @Test
    fun `processData returns Error when an exception occurs`() = runBlocking {
        val rawDataList = listOf(RawData())

        // Mocking
        every { dataValidator.validate(any()) } returns true
        every { dataTransformer.transform(any()) } throws RuntimeException("Transformation error")

        // Act
        val result = dataProcessor.processData(rawDataList)

        // Assert
        assertTrue(result is ProcessingResult.Error)
        assertEquals("Transformation error", (result as ProcessingResult.Error).exception.message)
    }
}
```

### Explanation of the Tests:
1. **Success Test**: Checks that if all data is valid and transforms successfully, a `ProcessingResult.Success` is returned and the data repository `saveAll` is called correctly.
2. **Partial Success Test**: Ensures that if some transformations are invalid, the result should indicate a partial success with appropriate data division.
3. **Empty Result Test**: Tests how the function