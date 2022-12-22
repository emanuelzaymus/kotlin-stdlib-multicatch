@file:Suppress("FunctionName")

package multicatch.problem

@Suppress("UNUSED_VARIABLE")
fun `try-catch with duplication`() {
    val result: Int = try {
        calculate()
    } catch (e: IllegalStateException) {
        println("Catching more exceptions: $e") // <-- Duplication
        -1                                      // <--
    } catch (e: IndexOutOfBoundsException) {
        println("Catching more exceptions: $e") // <-- Duplication
        -1                                      // <--
    } catch (e: Throwable) {
        println("Catching everything else: $e")
        -2
    }
}

@Suppress("UNUSED_VARIABLE")
private fun `runCatching with when clause`() {
    val result: Int = runCatching {
        calculate()
    }.recover { e ->
        when (e) {
            is IllegalStateException, is IndexOutOfBoundsException -> {
                println("Catching more exceptions: $e")
                -1
            }

            else -> {
                println("Catching everything else: $e")
                -2
            }
        }
    }.getOrThrow()
}

@Suppress("UNUSED_VARIABLE")
fun `try-catch with when clause`() {
    val result: Int = try {
        calculate()
    } catch (e: Throwable) {
        when (e) {
            is IllegalStateException, is IndexOutOfBoundsException -> {
                println("Catching more exceptions: $e")
                -1
            }

            else -> {
                println("Catching everything else: $e")
                -2
            }
        }
    }
}

fun main() {
    `try-catch with duplication`()

    `runCatching with when clause`()

    `try-catch with when clause`()
}

fun calculate(): Int = throw ArithmeticException()
