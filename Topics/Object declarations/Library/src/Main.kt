object Math {
    fun abs(value: Int): Int {
        if (value < 0) {
            return -value
        }
        return value
    }

    fun abs(value: Double): Double {
        if (value < 0) {
            return -value
        }
        return value
    }
}

fun main() {
    val list = listOf<Int>(1)
}