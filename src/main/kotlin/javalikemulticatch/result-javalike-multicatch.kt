package javalikemulticatch

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <T> trying(throwingBlock: () -> T): Result<T> {
    return runCatching(throwingBlock)
}

inline fun <T> Result<T>.catch(vararg exceptions: KClass<out Throwable>, catchBlock: (Throwable) -> T): Result<T> {
    exceptionOrNull()?.let { e ->
        if (exceptions.isEmpty() || exceptions.any { it.isInstance(e) }) {
            return recover(catchBlock)
        }
    }
    return this
}

inline fun <E : Throwable, T> Result<T>.catch(single: KClass<E>, catchBlock: (E) -> T): Result<T> {
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

infix fun <T> Result<T>.catch(catchBlock: (Throwable) -> T): Result<T> {
    return catch(Throwable::class, catchBlock = catchBlock)
}

inline fun <T> Result<T>.catchTrying(
    vararg exceptions: KClass<out Throwable>,
    catchBlock: (Throwable) -> T
): Result<T> {
    return runCatching {
        catch(*exceptions, catchBlock = catchBlock).getOrThrow()
    }
}

inline infix fun <T> Result<T>.catchTrying(catchBlock: (Throwable) -> T): Result<T> {
    return catchTrying(Throwable::class, catchBlock = catchBlock)
}

inline infix fun <T> Result<T>.finally(finallyBlock: () -> Unit): Result<T> {
    finallyBlock()
    return this
}

fun <T> Result<T>.throwIfNotCaught() {
    getOrThrow()
}

operator fun <T> Result<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return getOrThrow()
}
