package multicatch.stdlib

fun main() {
    val int: Int = runCatching {
        throw IllegalStateException()

        @Suppress("UNREACHABLE_CODE")
        100
    }.recover<IllegalStateException> {
        println(it)
        -1
    }.getOrThrow()

    println("int: $int")
}
