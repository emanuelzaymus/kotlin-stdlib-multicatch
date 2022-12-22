import multicatch.*
import java.util.*

fun divide(a: Int?, b: Int?): Int {
    // Produces IllegalArgumentException
    requireNotNull(a) { "A was null" }
    requireNotNull(b) { "B was null" }

    // Produces IllegalStateArgument
    check(a >= b) { "For this function A needs to be greater than B." }

    // Can produce division with zero.
    return a / b
}

fun `Catching only specific exceptions`() {
    val result: Int by runCatching {
        divide(10, null)
    }.recover(IllegalArgumentException::class) { e ->
        println("Problem with arguments: $e")
        -1
    }

    // Problem with arguments: java.lang.IllegalArgumentException: B was null
    // Result: -1
    println("Result: $result \n")
}

fun `Catching specific exceptions more than one time`() {
    var result: Int = -1
    val message: String by runCatching {
        result = divide(1, 0)
        "Success"
    }.recover(IllegalArgumentException::class, IllegalStateException::class) { e -> // Catches illegal argument / state
        println("Problem with arguments: $e")
        "Mistake"
    }.recover(ArithmeticException::class) { e -> // Catches ArithmeticException only
        println("Division by zero: $e")
        "Failure"
    }.recover { e -> // Catches everything
        println("Unexpected error: $e")
        "Fatal"
    }

    // Division by zero: java.lang.ArithmeticException: / by zero
    // Failure: -1
    println("$message: $result \n")
}

fun `Catching while recovery`() {
    val result: Int by runCatching {
        divide(0, 10)
    }.recoverCatching(IllegalArgumentException::class, IllegalStateException::class) { e ->
        println("Problem with arguments: $e")
        divide(10, 0)
    }.recover(ArithmeticException::class) { e -> // Catches ArithmeticException while catching new possible ex
        println("Division by zero: $e")
        -1
    }

    println("Result: $result \n")
}

fun `Will throw if exception is not handled`() {
    runCatching {
        divide(10, 0)
    }.recover(IllegalArgumentException::class) { e ->
        println("Problem with arguments: $e")
    }.getOrThrow()
    // ArithmeticException will be thrown because it is not handled

    // Exception in thread "main" java.lang.ArithmeticException: / by zero

//    println(result)
}

fun main() {

    `Catching only specific exceptions`()

    `Catching specific exceptions more than one time`()

    `Catching while recovery`()

    `Will throw if exception is not handled`()

//        .recover { e ->
//        println("Unexpected error: $e")
//        "Fatal"
//    }


    return

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

