fun findSerialNumberForGame(listGames: List<String>) {
    val reqName = readln()
    listGames.forEach {
        val (name, serialNumberForXbox, serialNumberForPC) = it.split("@".toRegex(), 3)
        if (name == reqName) {
            println("Code for Xbox - $serialNumberForXbox, for PC - $serialNumberForPC")
            return@forEach
        }
    }
}