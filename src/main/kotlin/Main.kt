import multicatch.recover
import multicatch.recoverCatching
import java.util.*

fun main(args: Array<String>) {

    val s = runCatching {
        throw NumberFormatException()
        "Success"
    }.recover(IllegalStateException::class, IndexOutOfBoundsException::class) { e ->
        println("First catch: $e")
        "Failure 1"
    }.recoverCatching(NumberFormatException::class) { e ->
        println("Second catch: $e")
        "Fail 2"
        throw Exception()
    }.recoverCatching { e ->
        println("Third catch: $e")
        throw EmptyStackException()
        "Failure 3"
    }.recover {
        println("Last recover")
        "Last recover str"
    }.also {
        println("Finnaly 2")
    }.getOrThrow()

    println(s)
}