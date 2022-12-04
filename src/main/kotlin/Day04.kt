fun main() {
    fun inputToRanges(input: List<String>) = input
        .map {
            it
                .split(",")
                .map { it.split("-").map(String::toInt) }
                .map { (a, b) -> IntRange(a, b) }
        }

    fun part1(input: List<String>) = inputToRanges(input)
        .count { (x, y) ->
            x.intersect(y).size in listOf(x.last - x.first + 1, y.last - y.first + 1)
        }

    fun part2(input: List<String>) = inputToRanges(input)
        .count { (x, y) ->
            x.intersect(y).isNotEmpty()
        }

    val testInput = readStrings("Day04_test")
    check(part1(testInput) == 2)

    val input = readStrings("Day04")
    println(part1(input))

    check(part2(testInput) == 4)
    println(part2(input))
}
