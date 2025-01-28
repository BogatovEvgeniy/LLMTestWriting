class TimedCache<K : Any, V : Any>(
    private val maxSize: Int,
    private val expirationTime: Duration
) {
    private data class CacheEntry<V>(
        val value: V,
        val timestamp: Instant
    )

    private val cache = LinkedHashMap<K, CacheEntry<V>>()
    private val lock = ReentrantReadWriteLock()

    fun put(key: K, value: V) {
        lock.write {
            cleanExpiredEntries()
            if (cache.size >= maxSize) {
                cache.entries.firstOrNull()?.let { cache.remove(it.key) }
            }
            cache[key] = CacheEntry(value, Instant.now())
        }
    }

    fun get(key: K): V? = lock.read {
        val entry = cache[key]
        when {
            entry == null -> null
            isExpired(entry) -> {
                lock.write { cache.remove(key) }
                null
            }
            else -> entry.value
        }
    }

    private fun isExpired(entry: CacheEntry<V>): Boolean =
        Duration.between(entry.timestamp, Instant.now()) > expirationTime

    private fun cleanExpiredEntries() {
        cache.entries.removeAll { isExpired(it.value) }
    }
}

