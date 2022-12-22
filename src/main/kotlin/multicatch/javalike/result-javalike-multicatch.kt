package multicatch.javalike

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Calls [throwingBlock] in which can be raised an [Exception] and returns result encapsulated in [Result].
 * See [runCatching].
 */
inline fun <R> trying(throwingBlock: () -> R): Result<R> {
    return runCatching(throwingBlock)
}

/**
 * Catches specified [exceptions] and handles them in [catchBlock].
 * If no exception is specified any exception will be caught.
 * See [recover].
 */
inline fun <R, T : R> Result<T>.catch(
    vararg exceptions: KClass<out Throwable>,
    catchBlock: (Throwable) -> R
): Result<R> {
    exceptionOrNull()?.let { e ->
        if (exceptions.isEmpty() || exceptions.any { it.isInstance(e) }) {
            return recover(catchBlock)
        }
    }
    return this
}

/**
 * Catches [single] exception and handles it in typed [catchBlock].
 * See [recover].
 */
inline fun <R, T, E> Result<T>.catch(
    single: KClass<E>,
    catchBlock: (E) -> R
): Result<R> where T : R,
                   E : Throwable {
    exceptionOrNull()?.let { e ->
        if (single.isInstance(e)) {
            return recover {
                @Suppress("UNCHECKED_CAST")
                catchBlock(e as E)
            }
        }
    }
    return this
}

/**
 * Catches any exception and handles it in [catchBlock].
 */
inline infix fun <R, T : R> Result<T>.catch(catchBlock: (Throwable) -> R): Result<R> {
    return catch(*emptyArray(), catchBlock = catchBlock)
}

/**
 * Catches specified [exceptions] while catching exception that could be raised in [catchBlock].
 */
inline fun <R, T : R> Result<T>.catchTrying(
    vararg exceptions: KClass<out Throwable>,
    catchBlock: (Throwable) -> R
): Result<R> {
    return runCatching {
        catch(*exceptions, catchBlock = catchBlock).getOrThrow()
    }
}

/**
 * Catches [single] exception and handles it in typed [catchBlock] while
 * catching exception that could be raised in [catchBlock].
 * See [catch].
 */
inline fun <R, T, E> Result<T>.catchTrying(
    single: KClass<E>,
    catchBlock: (E) -> R
): Result<R> where T : R,
                   E : Throwable {
    return runCatching {
        catch(single, catchBlock).getOrThrow()
    }
}

/**
 * Catches any exception and handles it in [catchBlock] while catching exception that could be raised in [catchBlock].
 */
inline infix fun <R, T : R> Result<T>.catchTrying(catchBlock: (Throwable) -> R): Result<R> {
    return catchTrying(*emptyArray(), catchBlock = catchBlock)
}

/**
 * Finally block will be called every time.
 */
inline infix fun <T> Result<T>.finally(finallyBlock: () -> Unit): Result<T> {
    finallyBlock()
    return this
}

/**
 * Should be called at the end of function call when there is chance that uncaught exception can be raised.
 * See [Result.getOrThrow].
 */
fun <T> Result<T>.throwIfNotCaught() {
    getOrThrow()
}

/**
 * Gets value by delegation. No necessity call [Result.getOrThrow] function.
 */
operator fun <R> Result<R>.getValue(thisRef: Any?, property: KProperty<*>): R {
    return getOrThrow()
}
