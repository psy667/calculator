package calculator

import kotlin.system.exitProcess

class CLI {
    private val interpreter = Interpreter()

    private fun exit() {
        println("Bye!")
        exitProcess(0)
    }

    private fun help() {
        println(
            """
        Basic operations: plus, minus, multiply, divide
            > 2 + 5 * 4 / 2 - 3
            < 9
        Parenthesis:
            > 2 * (20 / 4)
            < 10
        Variables:
            > a = 5
            > b = 10
            > c = a + b + 20
            > c
            < 35
        """.trimIndent()
        )
    }

    fun input(value: String) {
        if (value.startsWith('/')) {
            when (value.substring(1)) {
                "exit" -> exit()
                "help" -> help()
                else -> println("Unknown command")
            }
        } else {
            if (value.trim().isEmpty()) {
                return
            }
            try {
                val output = interpreter.run(value)

                if (output != null) {
                    println(output)
                }
            } catch (e: IllegalStateException) {
                println(e.message)
            }
        }

    }
}

fun main() {
    val cli = CLI()
    while (true) {
        val input = readLine()!!
        cli.input(input)
    }
}
