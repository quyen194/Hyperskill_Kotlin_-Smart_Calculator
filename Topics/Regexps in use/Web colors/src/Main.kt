fun main() {
    val text = readln()
    val regexColors = "#[0-9a-fA-F]{6}\\b".toRegex()
    val matchResult = regexColors.findAll(text).map { it.value }
    println(matchResult.joinToString(System.lineSeparator()))
}