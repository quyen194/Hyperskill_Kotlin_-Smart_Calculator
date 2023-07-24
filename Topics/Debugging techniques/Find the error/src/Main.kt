import java.util.Scanner

fun swapInts(ints: IntArray): IntArray {
    require(ints.size >= 2) { "Array should have at least 2 childs" }
    return intArrayOf(ints[1], ints[0])
}

fun main() {
    val scanner = Scanner(System.`in`)
    while (scanner.hasNextLine()) {
        var ints = intArrayOf(
            scanner.nextLine().toInt(),
            scanner.nextLine().toInt(),
        )
        ints = swapInts(ints)
        println(ints[0])
        println(ints[1])
    }
}
