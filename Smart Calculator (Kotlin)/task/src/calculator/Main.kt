package calculator

object CommandManager {
    var exit = false

    fun handle(cmd: String) {
        when (cmd) {
            "/exit" -> {
                println("Bye!")
                exit = true
            }
            "/help" -> println("The program calculates the sum/subtraction of numbers")
            else -> println("Unknown command")
        }
    }
}

object VariableManager {
    private val map = mutableMapOf<String, Int>()

    fun get(name: String): Int? = map[name]

    fun handle(exp: String): Boolean {
        val arr = exp.split("=")
        if (arr.size != 2) {
            return false
        }

        val (name, value) = arr.map { it.trim() }

        if (!Regex("[a-zA-Z]+").matches(name)) {
            return false
        }

        if (Regex("[+-]?\\d+").matches(value)) {
            map[name] = value.toInt()
            return true
        }

        if (Regex("[a-zA-Z]+").matches(value)) {
            if (!map.contains(value)) {
                return false
            }
            map[name] = map[value]!!
            return true
        }

        return false
    }
}

class Operation(pType: String) {
    object Type {
        const val ADD = '+'
        const val SUBTRACT = '-'
        const val MULTIPLE = '*'
        const val DIVIDE = '/'
        const val POWER = '^'
//        const val LEFT_PARENTHESIS = '('
//        const val RIGHT_PARENTHESIS = ')'

        fun priority(type: Char): Int {
            return when (type) {
                Type.ADD,
                Type.SUBTRACT -> 1
                Type.MULTIPLE,
                Type.DIVIDE -> 2
                Type.POWER -> 3
//                Type.LEFT_PARENTHESIS,
//                Type.RIGHT_PARENTHESIS -> 4
                else -> 0
            }
        }
    }

    private var type :Char? = null

    init {
        type = when (pType[0]) {
            Type.SUBTRACT -> if (pType.length % 2 == 0) Type.ADD else Type.SUBTRACT
            else -> pType[0]
        }
    }

    operator fun compareTo(ref: Operation): Int {
        return Type.priority(type!!) - Type.priority(ref.type!!)
    }

    fun perform(a: Int, b: Int): Int {
        return when (type) {
            Type.ADD -> a + b
            Type.SUBTRACT -> a - b
            Type.MULTIPLE -> a * b
            Type.DIVIDE -> a / b
            else -> 0
        }
    }
}

object Calculator {
    const val ITEM_TYPE_INVALID = -1
    const val ITEM_TYPE_NONE = 0
    const val ITEM_TYPE_VAR = 1
    const val ITEM_TYPE_NUM = 2
    const val ITEM_TYPE_OPE = 3
    const val ITEM_TYPE_LEFT_PARENTHESIS = 4
    const val ITEM_TYPE_RIGHT_PARENTHESIS = 5

    private fun detectType(input: String): Int {
        if (Regex("""[a-zA-Z]+""").matches(input)) {
            return ITEM_TYPE_VAR
        }

        if (Regex("""[+-]?\d+""").matches(input)) {
            return ITEM_TYPE_NUM
        }

        if (Regex("""[+-]+""").matches(input)) {
            return ITEM_TYPE_OPE
        }

        if (input == "(") {
            return ITEM_TYPE_LEFT_PARENTHESIS
        }

        if (input == ")") {
            return ITEM_TYPE_RIGHT_PARENTHESIS
        }

        return ITEM_TYPE_INVALID
    }

    private fun validateType(prev: Int, next: Int): Boolean {
        return when (prev) {
            ITEM_TYPE_NONE -> when (next) {
                ITEM_TYPE_VAR,
                ITEM_TYPE_NUM,
                ITEM_TYPE_LEFT_PARENTHESIS -> true
                else -> false
            }
            ITEM_TYPE_VAR,
            ITEM_TYPE_NUM -> when (next) {
                ITEM_TYPE_OPE,
                ITEM_TYPE_RIGHT_PARENTHESIS -> true
                else -> false
            }
            ITEM_TYPE_OPE,
            ITEM_TYPE_LEFT_PARENTHESIS-> when (next) {
                ITEM_TYPE_VAR,
                ITEM_TYPE_NUM,
                ITEM_TYPE_LEFT_PARENTHESIS -> true
                else -> false
            }
            ITEM_TYPE_RIGHT_PARENTHESIS -> when (next) {
                ITEM_TYPE_OPE,
                ITEM_TYPE_RIGHT_PARENTHESIS -> true
                else -> false
            }
            else -> false
        }
    }

    fun handle(exp: String): Boolean {
        val list = exp.split(Regex("\\s+"))

        var result: Int? = null
        var lastType = ITEM_TYPE_NONE
        var lastOpe: Operation? = null
        var lastValue: Int? = null

        list.forEach {
            val type = detectType(it)

            if (!validateType(lastType, type)) {
                return false
            }

            lastType = type

            when (type) {
                ITEM_TYPE_VAR -> {
                    lastValue = VariableManager.get(it)
                    if (lastValue === null) {
                        println("Unknown variable")
                        return true
                    }
                }
                ITEM_TYPE_NUM -> lastValue = it.toInt()
                ITEM_TYPE_OPE -> lastOpe = Operation(it)
            }

            if (result === null) {
                result = lastValue
                lastValue = null
            }

            if (lastOpe !== null && lastValue !== null) {
                result = lastOpe!!.perform(result!!, lastValue!!)
                lastOpe = null
                lastValue = null
            }
        }

        println(result)
        return true
    }
}

object ExpressionManager {
    fun handle(exp: String): Boolean {
        return if (exp.contains('=')) {
            VariableManager.handle(exp)
        }
        else {
            Calculator.handle(exp)
        }
    }
}

fun main() {
    loop@ while (true) {
        val input = readln()

        if (input.isBlank()) {
            continue
        }

        when (input[0]) {
            '/' -> CommandManager.handle(input)
            else -> {
                if (!ExpressionManager.handle(input)) {
                    println("Invalid expression")
                }
            }
        }

        if (CommandManager.exit) {
            break
        }
    }
}
