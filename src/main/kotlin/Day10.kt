import kotlin.math.abs

fun main() {
    fun xValues(input: List<String>) = input
        .map { it.split(" ") }
        .flatMap {
            when (it.first()) {
                "noop" -> listOf(0)
                "addx" -> listOf(0, it.last().toInt())
                else -> error("Unexpected instruction ${it.first()}")
            }
        }
        .let { it.toMutableList().apply { add(0, 1) } }
        .let { xChanges ->
            (1..xChanges.size).map { xChanges.take(it).sum() }
        }

    fun part1(input: List<String>) = xValues(input)
        .let {
            (20..220 step 40).map { cycle ->
                cycle * it[cycle - 1]
            }
        }
        .sum()

    fun part2(input: List<String>): String {
        val xValues = xValues(input)
        val output = StringBuilder()
        for (row in 0..5) {
            for (column in 0..39) {
                val cycle = (row * 40) + column + 1
                output.append(if (abs(xValues[cycle - 1] - column) <= 1) '#' else '.')
            }
            output.append('\n')
        }
        return "$output"
    }

    val testInput = readStrings("Day10_test")
    check(part1(testInput) == 13_140)

    val input = readStrings("Day10")
    println(part1(input))

    check(
        part2(testInput).trimIndent() == """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent()
    )
    println(part2(input))
}
