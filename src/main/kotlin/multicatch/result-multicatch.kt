package multicatch

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Catches specified [exceptions] and handles them in [transform] function.
 * If no exception is specified any exception will be caught.
 *
 * Returns original value if [this] is [Result.isSuccess], or transforms received exception using [transform] function
 * and encapsulates it in [Result.failure] if [this] is [Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception raised by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
inline fun <R, T : R> Result<T>.recover(
    vararg exceptions: KClass<out Throwable>,
    transform: (exception: Throwable) -> R
): Result<R> {
    exceptionOrNull()?.let { e ->
        if (exceptions.isEmpty() || exceptions.any { it.isInstance(e) }) {
            return recover(transform)
        }
    }
    return this
}

/**
 * Catches [singleException] and handles it in typed [transform] function.
 *
 * Returns original value if [this] is [Result.isSuccess], or transforms received exception using [transform] function
 * and encapsulates it in [Result.failure] if [this] is [Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception raised by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
inline fun <R, T, E> Result<T>.recover(
    singleException: KClass<E>,
    transform: (exception: E) -> R
): Result<R> where T : R,
                   E : Throwable {
    exceptionOrNull()?.let { e ->
        if (singleException.isInstance(e)) {
            return recover {
                @Suppress("UNCHECKED_CAST")
                transform(e as E)
            }
        }
    }
    return this
}

/**
 * Catches specified [exceptions] and handles them in typed [transform] function.
 * If no exception is specified catches everything.
 *
 * Returns original value if [this] is [Result.isSuccess], or transforms received exception using [transform] function
 * and encapsulates it in [Result.failure] if [this] is [Result.isFailure].
 *
 * This function catches any [Throwable] exception raised by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */
inline fun <R, T : R> Result<T>.recoverCatching(
    vararg exceptions: KClass<out Throwable>,
    transform: (exception: Throwable) -> R
): Result<R> {
    return if (exceptions.isEmpty()) {
        recoverCatching(transform)
    } else
        runCatching {
            recover(*exceptions, transform = transform).getOrThrow()
        }
}

/**
 * Catches [singleException] and handles it in typed [transform] function.
 *
 * Returns original value if [this] is [Result.isSuccess], or transforms received exception using [transform] function
 * and encapsulates it in [Result.failure] if [this] is [Result.isFailure].
 *
 * This function catches any [Throwable] exception raised by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */
inline fun <R, T, E> Result<T>.recoverCatching(
    singleException: KClass<E>,
    transform: (exception: E) -> R
): Result<R> where T : R,
                   E : Throwable {
    return runCatching {
        recover(singleException, transform).getOrThrow()
    }
}

/**
 * Gets value by delegation. No necessity to call [Result.getOrThrow] function.
 */
operator fun <R> Result<R>.getValue(thisRef: Any?, property: KProperty<*>): R {
    return getOrThrow() // TODO: remove
}