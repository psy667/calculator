package calculator

import java.math.BigInteger

open class Node

class LiteralNode(val value: BigInteger) : Node()
class ExpressionNode(val operator: Char, val left: Node, val right: Node) : Node()
class IdentifierNode(val name: String) : Node()
class DeclarationNode(val id: IdentifierNode, val init: Node) : Node()


class Parser(private val tokens: List<Token>) {
    private var cursor = 0

    fun parse(): Node {
        val first = expr()

        if (first is IdentifierNode && cursor < tokens.size && tokens[cursor] is DeclarationToken) {
            val id = IdentifierNode(first.name)
            cursor++
            try {
                val init = expr()
                return DeclarationNode(id, init)
            } catch (e: IllegalStateException) {
                throw IllegalStateException("Invalid assignment")
            }
        }

        return first
    }

    // E -> T + E | T - E | T
    private fun expr(): Node {
        var left: Node = term()

        while (cursor < tokens.size) {
            val operator = tokens[cursor]

            if (operator is DeclarationToken && cursor != 1) {
                throw IllegalStateException("Invalid assigment")
            }

            if (operator is ExpressionToken && (operator.operator == '+' || operator.operator == '-')) {
                cursor++
            } else {
                break
            }

            val right = this.term()

            left = ExpressionNode(operator.operator, left, right)
        }

        return left
    }

    // T -> F * T | F / T | F
    private fun term(): Node {
        var left: Node = factor()

        while (cursor < tokens.size) {
            val operator = tokens[cursor]

            if (operator is ExpressionToken && (operator.operator == '*' || operator.operator == '/')) {
                cursor++
            } else {
                break
            }

            val right = this.factor()

            left = ExpressionNode(operator.operator, left, right)
        }

        return left
    }

    // F -> N     | (E)
    private fun factor(): Node {
        val next: Token = tokens[cursor]
        val result: Node

        if (next is ExpressionToken && next.operator == ')') {
            throw IllegalStateException("Invalid expression")
        }

        if (next is ExpressionToken && next.operator == '(') {
            cursor++
            result = expr()
            val closingBracket: Token

            if (cursor < tokens.size) {
                closingBracket = tokens[cursor]
            } else {
                throw IllegalStateException("Invalid expression")
            }
            if (cursor < tokens.size && closingBracket is ExpressionToken && closingBracket.operator == ')') {
                cursor++
                return result
            }
            throw IllegalStateException("')' expected but $closingBracket")
        }
        cursor++
        if (next is LiteralToken) {
            return LiteralNode(next.value)
        } else if (next is ExpressionToken) {
            val unary = tokens[cursor]
            cursor++
            if (unary is LiteralToken) {
                return if (next.operator == '-') {
                    LiteralNode(-unary.value)
                } else {
                    LiteralNode(unary.value)
                }
            }
        } else if (next is IdentifierToken) {
            if (!Regex("""[a-zA-Z]*""").matches(next.name)) {
                throw IllegalStateException("Invalid identifier")
            }
            return IdentifierNode(next.name)
        }

        throw IllegalStateException("Unexpected token")
    }
}
