# kotlin-stdlib-multicatch

#### Augmentation of Kotlin's Result&lt;T> class to catch specific exceptions.

Attempt to solve the issue of **branching based on the type of the exception**
that was raised in `try-catch` expression on in the `runCatching { }` function.

### Current exception-type branching situation in Kotlin

```kotlin
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
```

```kotlin
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
```

Or `when` clause could also be used in `try-catch` expression.

---

#### I propose following solution based on extending `Result<T>` class from Kotlin's stdlib:

### Recovery from specific exceptions

```kotlin
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
```

### Recovery and catching

```kotlin
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
```

### Returning value

```kotlin
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
```

### Uncaught exception

```kotlin
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
```

For more information see the
[implementation](https://github.com/emanuelzaymus/kotlin-stdlib-multicatch/blob/main/src/main/kotlin/multicatch/kotlin/result-multicatch.kt)
.

Also check out my Java-like implementation of multi-catch in Kotlin with examples
[here in this repo](https://github.com/emanuelzaymus/kotlin-stdlib-multicatch/tree/main/src/main/kotlin/multicatch/javalike)
.
