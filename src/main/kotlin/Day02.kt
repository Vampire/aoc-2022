fun main() {
    val moveScores = mapOf(
        "X" to 1,
        "Y" to 2,
        "Z" to 3
    )

    val resultScores = mapOf(
        "A X" to 3,
        "A Y" to 6,
        "A Z" to 0,
        "B X" to 0,
        "B Y" to 3,
        "B Z" to 6,
        "C X" to 6,
        "C Y" to 0,
        "C Z" to 3
    )

    val strategyMapping = mapOf(
        "A X" to "A Z",
        "A Y" to "A X",
        "A Z" to "A Y",
        "B X" to "B X",
        "B Y" to "B Y",
        "B Z" to "B Z",
        "C X" to "C Y",
        "C Y" to "C Z",
        "C Z" to "C X"
    )

    fun part1(input: List<String>) = input.sumOf {
        moveScores[it.split(" ")[1]]!! + resultScores[it]!!
    }

    fun part2(input: List<String>) = part1(input.map { strategyMapping[it]!! })

    val testInput = readStrings("Day02_test")
    check(part1(testInput) == 15)

    val input = readStrings("Day02")
    println(part1(input))

    check(part2(testInput) == 12)
    println(part2(input))
}
