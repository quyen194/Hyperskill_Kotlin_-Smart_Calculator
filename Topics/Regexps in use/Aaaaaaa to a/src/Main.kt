fun main() {
    val text = readln()
    println(text.replace("[Aa]+".toRegex(), "a"))
}