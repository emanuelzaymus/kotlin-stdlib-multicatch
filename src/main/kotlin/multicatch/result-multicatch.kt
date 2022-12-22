package multicatch

import kotlin.reflect.KClass

/**
 * Catches specified exceptions. If no exception is specified catches everything.
 */
inline fun <T> Result<T>.recover(
    vararg exceptions: KClass<out Throwable>,
    transform: (exception: Throwable) -> T
): Result<T> {
    exceptionOrNull()?.let { e ->
        if (exceptions.isEmpty() || exceptions.any { it.isInstance(e) }) {
            return recover(transform)
        }
    }
    return this
}

/**
 * Catches specified exceptions while processing any exception caused by [transform].
 * If no exception is specified catches everything.
 */
inline fun <T> Result<T>.recoverCatching(
    vararg exceptions: KClass<out Throwable>,
    transform: (exception: Throwable) -> T
): Result<T> {
    if (exceptions.isEmpty()) {
        return recoverCatching(transform)
    }

    return runCatching {
        recover(*exceptions, transform = transform).getOrThrow()
    }
}
