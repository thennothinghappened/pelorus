package org.orca.pelorus.data.repository.cache

/**
 * Local cached data.
 */
sealed class CacheEntry<T> {

    /**
     * A cached entry.
     */
    data class Data<T>(val data: T) : CacheEntry<T>()

    /**
     * No entry is cached and will need to be re-fetched.
     */
    class NotCached<T> : CacheEntry<T>()

    inline fun <R> fold(
        onData: (value: T) -> R,
        onNotCached: () -> R
    ) = when(this) {
        is Data -> onData(data)
        is NotCached -> onNotCached()
    }

    inline fun <R : T> getOrElse(
        onNotCached: () -> R
    ) = when(this) {
        is Data -> data
        is NotCached -> onNotCached()
    }

}
