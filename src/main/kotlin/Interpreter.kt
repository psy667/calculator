package calculator

import java.math.BigInteger

class Interpreter {
    private val memory = mutableMapOf<String, BigInteger>()

    private fun eval(node: Node): BigInteger? {
        if (node is LiteralNode) {
            return node.value
        }
        if (node is ExpressionNode) {
            val left = eval(node.left)!!
            val right = eval(node.right)!!

            return when (node.operator) {
                '+' -> left.plus(right)
                '-' -> left.minus(right)
                '*' -> left.multiply(right)
                '/' -> left.divide(right)
                else -> null
            }
        }
        if (node is DeclarationNode) {
            val result = eval(node.init)
            memory[node.id.name] = result!!
            return null
        }
        if (node is IdentifierNode) {
            if (memory.containsKey(node.name)) {
                return memory[node.name]!!
            } else {
                throw IllegalStateException("Unknown variable")
            }
        }

        return null
    }

    fun run(input: String): String? {
        val tokenizer = Tokenizer(input, 0)
        val tokens = mutableListOf<Token>()

        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.getNextToken()
            if (token != null) {
                tokens.add(token)
            }
        }

        val ast = Parser(tokens).parse()

        return eval(ast)?.toString()
    }
}
