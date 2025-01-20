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

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val validElements = elements.filter { validator(it) }
        elements.filterNot { validator(it) }.forEach { onValidationError(it) }
        return internalList.addAll(index, validElements)
    }

    override fun clear() {
        internalList.clear()
    }

    override fun remove(element: T): Boolean {
        return internalList.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return internalList.removeAll(elements)
    }

    override fun removeAt(index: Int): T {
        return internalList.removeAt(index)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return internalList.retainAll(elements)
    }

    override fun set(index: Int, element: T): T {
        if (validator(element)) {
            return internalList.set(index, element)
        } else {
            onValidationError(element)
            throw IllegalArgumentException("Invalid element")
        }
    }

    // Read-only operations
    override val size: Int get() = internalList.size

    override fun contains(element: T): Boolean = internalList.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = internalList.containsAll(elements)

    override fun get(index: Int): T = internalList[index]

    override fun indexOf(element: T): Int = internalList.indexOf(element)

    override fun isEmpty(): Boolean = internalList.isEmpty()

    override fun lastIndexOf(element: T): Int = internalList.lastIndexOf(element)

    // Iterator operations
    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        private val iter = internalList.iterator()

        override fun hasNext(): Boolean = iter.hasNext()
        override fun next(): T = iter.next()
        override fun remove() {
            iter.remove()
        }
    }

    override fun listIterator(): MutableListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<T> = object : MutableListIterator<T> {
        private val iter = internalList.listIterator(index)

        override fun hasNext(): Boolean = iter.hasNext()
        override fun hasPrevious(): Boolean = iter.hasPrevious()
        override fun next(): T = iter.next()
        override fun nextIndex(): Int = iter.nextIndex()
        override fun previous(): T = iter.previous()
        override fun previousIndex(): Int = iter.previousIndex()

        override fun add(element: T) {
            if (validator(element)) {
                iter.add(element)
            } else {
                onValidationError(element)
                throw IllegalArgumentException("Invalid element")
            }
        }

        override fun remove() {
            iter.remove()
        }

        override fun set(element: T) {
            if (validator(element)) {
                iter.set(element)
            } else {
                onValidationError(element)
                throw IllegalArgumentException("Invalid element")
            }
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        ValidatedList(validator, onValidationError).also { newList ->
            newList.addAll(internalList.subList(fromIndex, toIndex))
        }
}