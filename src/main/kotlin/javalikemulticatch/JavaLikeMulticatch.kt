@file:Suppress("FunctionName")

package javalikemulticatch

fun `Catching specific exceptions`() {
    trying {
        throw IllegalArgumentException()
    }.catch(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
    }.catch(IllegalArgumentException::class) { e: IllegalArgumentException ->
        println("Catching one exception: $e")
    } catch { e ->
        println("Catching everything else: $e")
    } finally {
        println("Finally block called")
    }

    // Catching one exception: java.lang.IllegalArgumentException
    // Finally block called
}

fun `Catching and retrying`() {
    trying {
        throw IllegalStateException()
    }.catchTrying(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
        throw Exception("Thrown inside")
    } catch { e ->
        println("Catching everything else: $e")
    }

    // Catching specific exceptions only: java.lang.IllegalStateException
    // Catching everything else: java.lang.Exception: Thrown inside
}

fun `Returning value be delegation`() {
    val message by trying {
        execute()
        "Success"
    }.catch(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
        "Failed with exception"
    } catchTrying { e ->
        println("Catching everything else: $e")
        "Totally failed"
    } finally {
        println("Finally block called")
    }

    println(message)
    // Catching everything else: java.lang.Exception
    // Finally block called
    // Totally failed
}

fun `Returning value by assignment`() {
    val result = trying {
        calculate()
    }.catch(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("Catching specific exceptions only: $e")
        -1
    }.catchTrying { e ->
        println("Catching everything else: $e")
        -2
    }.getOrThrow() // getOrThrow() is alternative to 'by delegation' call

    println("Result: $result")
    // Catching everything else: java.lang.ArithmeticException
    // Result: -2
}

fun `Uncaught exception`() {
    trying {
        throw Exception()
    }.catchTrying(IllegalStateException::class) { e: IllegalStateException ->
        println("Catching specific exceptions only: $e")
    }.throwIfNotCaught() // We need to call throwIfNotCaught in case exception was not caught.

    // Exception in thread "main" java.lang.Exception
    // at javalikemulticatch.JavaLikeMulticatchKt.Uncaught exception(JavaLikeMulticatch.kt:..)
    // at javalikemulticatch.JavaLikeMulticatchKt.main(JavaLikeMulticatch.kt:..)
    // at javalikemulticatch.JavaLikeMulticatchKt.main(JavaLikeMulticatch.kt)
}

fun main() {
    `Catching specific exceptions`().also { println() }

    `Catching and retrying`().also { println() }

    `Returning value be delegation`().also { println() }

    `Returning value by assignment`().also { println() }

    `Uncaught exception`().also { println() }
}

private fun execute(): Unit = throw Exception()
private fun calculate(): Int = throw ArithmeticException()
