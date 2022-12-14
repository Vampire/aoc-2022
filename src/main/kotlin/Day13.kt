fun main() {
    fun getNextList(s: String): String {
        require(s.first() == '[')
        var level = 0
        return s.takeWhile {
            0 < when (it) {
                '[' -> ++level
                ']' -> --level
                else -> level
            }
        } + ']'
    }

    fun parseList(list: String): List<Any> {
        require(list.first() == '[')
        require(list.last() == ']')
        var remainingListContents = getNextList(list)
            .drop(1)
            .dropLast(1)
        return buildList {
            while (remainingListContents.isNotEmpty()) {
                add(
                    when (remainingListContents.first()) {
                        in '0'..'9' -> remainingListContents
                            .substringBefore(',')
                            .also {
                                remainingListContents = remainingListContents
                                    .substringAfter(',', "")
                            }
                            .toInt()

                        '[' -> getNextList(remainingListContents)
                            .also {
                                remainingListContents = remainingListContents
                                    .substringAfter(it)
                                    .substringAfter(',')
                            }
                            .let { parseList(it) }

                        else -> error("Unexpected list contents: $remainingListContents")
                    }
                )
            }
        }
    }

    operator fun List<*>.compareTo(other: List<*>): Int {
        for (i in indices) {
            if (i >= other.size) {
                return 1
            }

            val a = get(i)
            val b = other[i]
            when {
                (a is Int) && (b is Int) -> if (a != b) return a - b
                (a is List<*>) && (b is List<*>) -> if (a != b) return a.compareTo(b)
                (a is List<*>) -> if (a != listOf(b)) return a.compareTo(listOf(b))
                (b is List<*>) -> if (listOf(a) != b) return listOf(a).compareTo(b)
            }
        }
        if (size < other.size) {
            return -1
        }
        return 0
    }

    fun part1(input: List<String>) = input
        .asSequence()
        .filter { it.isNotBlank() }
        .map { parseList(it) }
        .chunked(2)
        .mapIndexed { i, (a, b) ->
            if (a < b) (i + 1) else 0
        }
        .sum()

    fun part2(input: List<String>): Int {
        val dividers = listOf(listOf(listOf(2)), listOf(listOf(6)))
        return input
            .asSequence()
            .filter { it.isNotBlank() }
            .map { parseList(it) }
            .let { it + dividers }
            .sortedWith { a, b -> a.compareTo(b) }
            .toList()
            .let { packets ->
                dividers.map { packets.indexOf(it) + 1 }.reduce(Int::times)
            }
    }

    val testInput = readStrings("Day13_test")
    check(part1(testInput) == 13)

    val input = readStrings("Day13")
    println(part1(input))

    check(part2(testInput) == 140)
    println(part2(input))
}
