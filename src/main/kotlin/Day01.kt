fun main() {
    fun calorySums(caloryList: List<String>): List<Int> {
        var calories = 0
        val calorySums = caloryList.mapNotNull {
            if (it.isEmpty()) {
                return@mapNotNull calories.also { calories = 0 }
            } else {
                calories += it.toInt()
                return@mapNotNull null
            }
        }
        return calorySums
    }

    fun part1(input: List<String>) = calorySums(input).max()

    fun part2(input: List<String>) = calorySums(input).sortedDescending().take(3).sum()

    val testInput = readStrings("Day01_test")
    check(part1(testInput) == 24_000)

    val input = readStrings("Day01")
    println(part1(input))

    check(part2(testInput) == 45_000)
    println(part2(input))
}
