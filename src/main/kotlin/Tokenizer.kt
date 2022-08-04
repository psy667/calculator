package calculator

import java.math.BigInteger


open class Token
class ExpressionToken(val operator: Char) : Token()
class LiteralToken(val value: BigInteger) : Token()
class IdentifierToken(val name: String) : Token()
class DeclarationToken : Token()
class EOFToken : Token()


class Tokenizer(private val string: String, private var cursor: Int) {
    private var parenthesisBalance = 0

    fun hasMoreTokens(): Boolean {
        return cursor < string.length
    }

    fun getNextToken(): Token? {
        val numberRegex = Regex("""\d""")
        val identifierRegex = Regex("""[\wА-Яа-я]""")
        val operatorRegex = Regex("""[+-/*]""")
        val parenthesisRegex = Regex("""[()]""")
        val spaceRegex = Regex("""\s""")
        val declarationRegex = Regex("""=""")

        if (!hasMoreTokens()) {
            return null
        }

        if (numberRegex.matches(string[cursor].toString())) {
            var number = ""
            while (hasMoreTokens() && numberRegex.matches(string[cursor].toString())) {
                number += string[cursor]
                cursor++
            }
            if (hasMoreTokens() && Regex("""[a-zA-Z]""").matches(string[cursor].toString())) {
                throw IllegalStateException("Invalid identifier")
            }
            return LiteralToken(number.toBigInteger())
        }

        if (parenthesisRegex.matches(string[cursor].toString())) {
            val parenthesis = string[cursor]
            if (parenthesis == '(') {
                parenthesisBalance++
            }
            if (parenthesis == ')') {
                parenthesisBalance--
            }

            if (parenthesisBalance < 0) {
                throw IllegalStateException("Invalid expression")
            }
            cursor++
            return ExpressionToken(parenthesis)
        }

        if (operatorRegex.matches(string[cursor].toString())) {
            var operator: Char? = null

            while (hasMoreTokens() && operatorRegex.matches(string[cursor].toString())) {
                val cur = string[cursor]

                operator = if (operator != null && operator == '-' && cur == '-') {
                    '+'
                } else if (cur == '-') {
                    if (operator != null && operator != '+' && operator != '-') {
                        throw IllegalStateException("Invalid expression")
                    }
                    '-'
                } else {
                    if (operator != null && operator != '+') {
                        throw IllegalStateException("Invalid expression")
                    }
                    string[cursor]
                }

                cursor++
            }

            return ExpressionToken(operator ?: '+')
        }
        if (spaceRegex.matches(string[cursor].toString())) {
            cursor++
            return getNextToken()
        }

        if (declarationRegex.matches(string[cursor].toString())) {
            cursor++
            return DeclarationToken()
        }

        if (identifierRegex.matches(string[cursor].toString())) {
            var idName = ""
            while (hasMoreTokens() && identifierRegex.matches(string[cursor].toString())) {
                idName += string[cursor]
                cursor++
            }
            return IdentifierToken(idName)
        }

        cursor = string.length
        return EOFToken()
    }
}
