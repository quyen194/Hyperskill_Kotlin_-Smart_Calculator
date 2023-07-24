package calculator

import java.util.*
import kotlin.math.pow

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

object Calculator {
    open class Operator(private val priority: Int) {
        operator fun compareTo(ref: Operator): Int {
            return priority - ref.priority
        }
    }
    class OpeAdd(): Operator(1)
    class OpeSubtract(): Operator(1)
    class OpeMultiple(): Operator(3)
    class OpeDivide(): Operator(3)
    class OpePower(): Operator(4)
    class OpeUnaryMinus(): Operator(5)
    class LeftParenthesis()
    class RightParenthesis()
    class Number(val value: Int)
    class Variable(val name: String)

    private fun token2Object(token: String): Any? {
        if (Regex("""[a-zA-Z]+""").matches(token)) {
            return Variable(token)
        }

        if (Regex("""\d+""").matches(token)) {
            return Number(token.toInt())
        }

        return when(token) {
            "+" -> OpeAdd()
            "-" -> OpeSubtract()
            "*" -> OpeMultiple()
            "/" -> OpeDivide()
            "^" -> OpePower()
            "(" -> LeftParenthesis()
            ")" -> RightParenthesis()
            else -> null
        }
    }

    fun handle(exp: String): Boolean {
        // remove all spaces
        var optExp = exp
            .replace(" ", "")
            .replace("\r", "")
            .replace("\n", "")
        // optimize operator
        while (true) {
            val tempExp = optExp
                .replace("++", "+")
                .replace("--", "+")
                .replace("+-", "-")
                .replace("-+", "-")
            if (tempExp == optExp) {
                break
            }
            optExp = tempExp
        }

        // extract token from expression
        // https://stackoverflow.com/questions/3373885/splitting-a-simple-maths-expression-with-regex
        val regex = "(?<=op)|(?=op)".replace("op", "[-+*/()^]")
        val list = optExp
            .split(regex.toRegex())
            .filter { it.isNotEmpty() }
            .toMutableList()

        // convert token to object
        val queue = ArrayDeque<Any>()
        list.forEach {
            val obj = token2Object(it)
            if (obj === null) {
                return false
            }

            if (obj is Variable) {
                val value = VariableManager.get(obj.name)
                if (value !== null) {
                    queue.add(Number(value))
                }
                else {
                    println("Unknown variable")
                    return true
                }
            }
            else {
                queue.add(obj)
            }
        }

        // process subtract or unary minus
        val queue2 = ArrayDeque<Any>()
        queue.forEach {
            if (it is OpeSubtract) {
                if (queue2.isEmpty() ||
                    queue2.last() is Operator ||
                    queue2.last() is LeftParenthesis) {
                    queue2.add(OpeUnaryMinus())
                    return@forEach
                }
            }
            queue2.add(it)
        }

        // convert infix notation to RPN
        val rpnStack = ArrayDeque<Any>()
        val opeStack = ArrayDeque<Any>()
        queue2.forEach {
            when (it) {
                is Number -> rpnStack.push(it)
                is Operator -> {
                    if (opeStack.isEmpty() || opeStack.peek() is LeftParenthesis) {
                        opeStack.push(it)
                    }
                    else {
                        while (opeStack.isNotEmpty()) {
                            if (opeStack.peek() is LeftParenthesis ||
                                it > opeStack.peek() as Operator) {
                                break
                            }
                            rpnStack.push(opeStack.pop())
                        }
                        opeStack.push(it)
                    }
                }
                is LeftParenthesis -> opeStack.push(it)
                is RightParenthesis -> {
                    while (opeStack.isNotEmpty()) {
                        val item = opeStack.pop()
                        if (item !is LeftParenthesis) {
                            rpnStack.push(item)
                        }
                        else {
                            break
                        }
                    }

                    if (opeStack.isEmpty()) {
                        // unbalanced brackets
                        return false
                    }
                }
            }
        }

        while (opeStack.isNotEmpty()) {
            val ope = opeStack.pop()
            if (ope is LeftParenthesis) {
                // unbalanced brackets
                return false
            }
            rpnStack.push(ope)
        }

        // calculate rpn
        val resultStack = ArrayDeque<Int>()
        while (rpnStack.isNotEmpty()) {
            val it = rpnStack.pollLast()

            when (it) {
                is OpeAdd,
                is OpeSubtract,
                is OpeMultiple,
                is OpeDivide,
                is OpePower -> {
                    if (resultStack.size < 2) {
                        return false
                    }
                }
                is OpeUnaryMinus -> {
                    if (resultStack.size < 1) {
                        return false
                    }
                }
            }

            when (it) {
                is Number -> resultStack.push(it.value)
                is OpeAdd -> {
                    val b = resultStack.pop()
                    val a = resultStack.pop()
                    resultStack.push(a + b)
                }
                is OpeSubtract -> {
                    val b = resultStack.pop()
                    val a = resultStack.pop()
                    resultStack.push(a - b)
                }
                is OpeMultiple -> {
                    val b = resultStack.pop()
                    val a = resultStack.pop()
                    resultStack.push(a * b)
                }
                is OpeDivide -> {
                    val b = resultStack.pop()
                    val a = resultStack.pop()
                    resultStack.push(a / b)
                }
                is OpePower -> {
                    val b = resultStack.pop()
                    val a = resultStack.pop()
                    resultStack.push(a.toDouble().pow(b).toInt())
                }
                is OpeUnaryMinus -> {
                    val a = resultStack.pop()
                    resultStack.push(-a)
                }
            }
        }

        if (resultStack.size != 1) {
            return false
        }

        println(resultStack.pop())

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
