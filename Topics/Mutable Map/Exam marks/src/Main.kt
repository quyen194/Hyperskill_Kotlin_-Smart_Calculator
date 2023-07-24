fun main() {
    val studentsMarks = mutableMapOf<String, Int>()
    while (true) {
        val name = readln()
        if (name == "stop") {
            break
        }
        val score = readln().toInt()
        studentsMarks.putIfAbsent(name, score)
    }
    println(studentsMarks)
}