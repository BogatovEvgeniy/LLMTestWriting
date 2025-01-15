class ValidatedList<T>(
    private val validator: (T) -> Boolean,
    private val onValidationError: (T) -> Unit = {}
) : MutableList<T> {
    private val internalList = mutableListOf<T>()

    override fun add(element: T): Boolean {
        return if (validator(element)) {
            internalList.add(element)
            true
        } else {
            onValidationError(element)
            false
        }
    }

    override fun add(index: Int, element: T) {
        if (validator(element)) {
            internalList.add(index, element)
        } else {
            onValidationError(element)
            throw IllegalArgumentException("Invalid element")
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val validElements = elements.filter { validator(it) }
        elements.filterNot { validator(it) }.forEach { onValidationError(it) }
        return internalList.addAll(validElements)
    }

    // ... implement other MutableList methods
    override val size: Int get() = internalList.size
    override fun contains(element: T): Boolean = internalList.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = internalList.containsAll(elements)
    override fun get(index: Int): T = internalList[index]
    override fun indexOf(element: T): Int = internalList.indexOf(element)
    override fun isEmpty(): Boolean = internalList.isEmpty()
    override fun iterator(): MutableIterator<T> = internalList.iterator()
    override fun lastIndexOf(element: T): Int = internalList.lastIndexOf(element)
    override fun listIterator(): MutableListIterator<T> = internalList.listIterator()
    override fun listIterator(index: Int): MutableListIterator<T> = internalList.listIterator(index)
    override fun removeAt(index: Int): T = internalList.removeAt(index)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = internalList.subList(fromIndex, toIndex)
    override fun set(index: Int, element: T): T {
        if (validator(element)) {
            return internalList.set(index, element)
        } else {
            onValidationError(element)
            throw IllegalArgumentException("Invalid element")
        }
    }
    override fun retainAll(elements: Collection<T>): Boolean = internalList.retainAll(elements)
    override fun removeAll(elements: Collection<T>): Boolean = internalList.removeAll(elements)
    override fun remove(element: T): Boolean = internalList.remove(element)
    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val validElements = elements.filter { validator(it) }
        elements.filterNot { validator(it) }.forEach { onValidationError(it) }
        return internalList.addAll(index, validElements)
    }
    override fun clear() = internalList.clear()
}