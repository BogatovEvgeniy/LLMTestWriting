class DataProcessor(
    private val dataValidator: DataValidator,
    private val dataTransformer: DataTransformer,
    private val dataRepository: DataRepository
) {
    suspend fun processData(input: List<RawData>): ProcessingResult {
        return try {
            coroutineScope {
                val validatedData = input
                    .filter { dataValidator.validate(it) }
                    .map { async { dataTransformer.transform(it) } }
                    .awaitAll()

                when {
                    validatedData.isEmpty() -> ProcessingResult.Empty
                    validatedData.any { it.isError } -> ProcessingResult.PartialSuccess(
                        successful = validatedData.filter { !it.isError },
                        failed = validatedData.filter { it.isError }
                    )
                    else -> {
                        dataRepository.saveAll(validatedData)
                        ProcessingResult.Success(validatedData)
                    }
                }
            }
        } catch (e: Exception) {
            ProcessingResult.Error(e)
        }
    }
}

sealed class ProcessingResult {
    data class Success(val data: List<TransformedData>) : ProcessingResult()
    data class PartialSuccess(
        val successful: List<TransformedData>,
        val failed: List<TransformedData>
    ) : ProcessingResult()
    data class Error(val exception: Exception) : ProcessingResult()
    object Empty : ProcessingResult()
}
