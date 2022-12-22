package javalikemulticatch

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <R> trying(throwingBlock: () -> R): Result<R> {
    return runCatching(throwingBlock)
}

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

infix fun <R, T : R> Result<T>.catch(catchBlock: (Throwable) -> R): Result<R> {
    return catch(*emptyArray(), catchBlock = catchBlock)
}

inline fun <R, T : R> Result<T>.catchTrying(
    vararg exceptions: KClass<out Throwable>,
    catchBlock: (Throwable) -> R
): Result<R> {
    return runCatching {
        catch(*exceptions, catchBlock = catchBlock).getOrThrow()
    }
}

inline infix fun <R, T : R> Result<T>.catchTrying(catchBlock: (Throwable) -> R): Result<R> {
    return catchTrying(*emptyArray(), catchBlock = catchBlock)
}

inline infix fun <T> Result<T>.finally(finallyBlock: () -> Unit): Result<T> {
    finallyBlock()
    return this
}

fun <T> Result<T>.throwIfNotCaught() {
    getOrThrow()
}

operator fun <R> Result<R>.getValue(thisRef: Any?, property: KProperty<*>): R {
    return getOrThrow()
}
