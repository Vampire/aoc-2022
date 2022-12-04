fun main() {
    fun part1(input: List<String>, amountOfDistinct: Int = 4) = input[0]
        .toCharArray()
        .asSequence()
        .withIndex()
        .windowed(amountOfDistinct)
        .find { markerCandidate -> markerCandidate.distinctBy { it.value }.size == amountOfDistinct }!!
        .let { it.last().index + 1 }

    fun part2(input: List<String>) = part1(input, amountOfDistinct = 14)

    val testInput1 = readStrings("Day06_test1")
    check(part1(testInput1) == 7)

    val testInput2 = readStrings("Day06_test2")
    check(part1(testInput2) == 5)

    val testInput3 = readStrings("Day06_test3")
    check(part1(testInput3) == 6)

    val testInput4 = readStrings("Day06_test4")
    check(part1(testInput4) == 10)

    val testInput5 = readStrings("Day06_test5")
    check(part1(testInput5) == 11)

    val input = readStrings("Day06")
    println(part1(input))

    check(part2(testInput1) == 19)
    check(part2(testInput2) == 23)
    check(part2(testInput3) == 23)
    check(part2(testInput4) == 29)
    check(part2(testInput5) == 26)
    println(part2(input))
}
