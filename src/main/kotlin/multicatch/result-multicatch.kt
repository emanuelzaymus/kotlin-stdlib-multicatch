package multicatch

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Catches specified [exceptions]. If no exception is specified catches everything.
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
 * Catches [singleException] exception and handles it in typed [transform].
 * See [recover].
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
 * Catches specified [exceptions] while processing any exception caused by [transform].
 * If no exception is specified catches everything.
 */
inline fun <R, T : R> Result<T>.recoverCatching(
    vararg exceptions: KClass<out Throwable>,
    transform: (exception: Throwable) -> R
): Result<R> {
    if (exceptions.isEmpty()) {
        return recoverCatching(transform)
    }

    return runCatching {
        recover(*exceptions, transform = transform).getOrThrow()
    }
}

/**
 * Catches [singleException] exception and handles it in typed [transform] while catching exception that could be raised in [transform].
 * See [recover].
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
 * Gets value by delegation. No necessity call [Result.getOrThrow] function.
 */
operator fun <R> Result<R>.getValue(thisRef: Any?, property: KProperty<*>): R {
    return getOrThrow() // TODO: remove
}