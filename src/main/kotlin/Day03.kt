fun main() {
    val priorities = ('a'..'z') + ('A'..'Z')

    fun part1(input: List<String>) = input
        .flatMap {
            it
                .chunkedSequence(it.length / 2)
                .map(String::toCharArray)
                .reduce { a, b -> a.intersect(b.asIterable().toSet()).toCharArray() }
                .map(priorities::indexOf)
                .map(Int::inc)
        }
        .sum()

    fun part2(input: List<String>) = input
        .asSequence()
        .chunked(3)
        .flatMap {
            it
                .asSequence()
                .map(String::toCharArray)
                .reduce { a, b -> a.intersect(b.asIterable().toSet()).toCharArray() }
                .map(priorities::indexOf)
                .map(Int::inc)
        }
        .sum()

    val testInput = readStrings("Day03_test")
    check(part1(testInput) == 157)

    val input = readStrings("Day03")
    println(part1(input))

    check(part2(testInput) == 70)
    println(part2(input))
}
