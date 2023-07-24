import java.math.BigInteger

const val ONE_HUNDRED = 100L

fun main() {
    val a = readln().toBigInteger()
    val b = readln().toBigInteger()
    val sum = a + b
    val oneHundred = BigInteger.valueOf(ONE_HUNDRED)
    val a1 = a * oneHundred / sum
    val b1 = b * oneHundred / sum
    println("$a1% $b1%")
}