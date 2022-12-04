fun main() {
    val movePattern = """move (?<amount>\d++) from (?<source>\d) to (?<target>\d)""".toPattern()

    fun part1(input: List<String>, rearrange: Boolean = true): String {
        val stackLines = input.takeWhile(String::isNotBlank).asReversed()
        val stackNumbersLine = stackLines[0]
        val stackNumbers = stackNumbersLine.trim().split(" ++".toPattern())
        val stacks = stackNumbers.associateWith { stackNumber ->
            val stackIndex = stackNumbersLine.indexOf(stackNumber)
            stackLines
                .drop(1)
                .map { if (stackIndex < it.length) it[stackIndex] else ' ' }
                .filter { it != ' ' }
                .toMutableList()
        }

        input
            .dropWhile(String::isNotBlank)
            .drop(1)
            .filter(String::isNotBlank)
            .forEach {
                val matchResult = movePattern.matcher(it).apply { check(matches()) }
                val amount = matchResult.group("amount").toInt()
                val source = matchResult.group("source")
                val target = matchResult.group("target")
                if (rearrange) {
                    amount.downTo(1).forEach {
                        stacks[target]!!.add(stacks[source]!!.removeLast())
                    }
                } else {
                    stacks[target]!!.addAll(amount.downTo(1).map {
                        stacks[source]!!.removeLast()
                    }.asReversed())
                }
            }
        return stackNumbers
            .map { stacks[it] }
            .joinToString(separator = "") {
                it!!.last().toString()
            }
    }

    fun part2(input: List<String>) = part1(input, rearrange = false)

    val testInput = readStrings("Day05_test", trim = false)
    check(part1(testInput) == "CMZ")

    val input = readStrings("Day05", trim = false)
    println(part1(input))

    check(part2(testInput) == "MCD")
    println(part2(input))

    check(part1(input) == "VWLCWGSDQ")
}
