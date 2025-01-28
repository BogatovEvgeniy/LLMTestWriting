class FibonacciGenerator(private val limit: Int) {
    private val fibonacciSequence: MutableList<Int> = mutableListOf(0, 1)

    init {
        generateSequence()
    }
    private fun generateSequence() {
        var a = 0
        var b = 1

        while (true) {
            val next = a + b
            if (next > limit) break
            fibonacciSequence.add(next)
            a = b
            b = next
        }
    }
    fun getSequence(): List<Int> {
        return fibonacciSequence
    }
    fun isFibonacciNumber(number: Int): Boolean {
        return fibonacciSequence.contains(number)
    }
}

