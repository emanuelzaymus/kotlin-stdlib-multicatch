@file:Suppress("FunctionName")

package multicatch.kotlin

fun `Recovery from specific exceptions`() {
    runCatching {
        throw IllegalArgumentException()
    }.recover(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
    }.recover(IllegalArgumentException::class) { e: IllegalArgumentException ->
        println("Catching one exception: $e")
    }.recover { e ->
        println("Catching everything else: $e")
    }

    // Catching one exception: java.lang.IllegalArgumentException
}

fun `Recovery and catching`() {
    runCatching {
        throw IllegalStateException()
    }.recoverCatching(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
        throw Exception("Thrown inside")
    }.recoverCatching(IllegalArgumentException::class) { e: IllegalArgumentException ->
        println("Catching one exception: $e")
        throw RuntimeException("Thrown inside")
    }.recover { e ->
        println("Catching everything else: $e")
    }

    // Catching specific exceptions only: java.lang.IllegalStateException
    // Catching everything else: java.lang.Exception: Thrown inside
}

fun `Returning value`() {
    val result: Int = runCatching {
        calculate() // throws ArithmeticException
    }.recover(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
        -1
    }.recover { e ->
        println("Catching everything else: $e")
        -2
    }.getOrThrow()

    println("Result: $result")
    // Catching everything else: java.lang.ArithmeticException
    // Result: -2
}

fun `Uncaught exception`() {
    runCatching {
        throw Exception()
    }.recover(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
    }.recover(ArithmeticException::class) { e: ArithmeticException ->
        println("Catching one exception: $e")
    }.getOrThrow() // We need to call getOrThrow() so that the uncaught exception is thrown.

    // Exception in thread "multicatch.kotlin.main" java.lang.Exception
    //     at multicatch.kotlin.MulticatchExamplesKt.`Uncaught exception`(MulticatchExamples.kt:..)
    //     at multicatch.kotlin.MulticatchExamplesKt.main(MulticatchExamples.kt:..)
    //     at multicatch.kotlin.MulticatchExamplesKt.main(MulticatchExamples.kt)
}

fun main() {
    `Recovery from specific exceptions`().also { println() }

    `Recovery and catching`().also { println() }

    `Returning value`().also { println() }

    `Uncaught exception`().also { println() }
}

private fun calculate(): Int = throw ArithmeticException()
