fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readStrings("Day01_test")
    check(part1(testInput) == 1)

    val input = readStrings("Day01")
    println(part1(input))
    println(part2(input))
}
