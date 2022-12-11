fun main() {
    fun part1(input: List<String>, ropeLength: Int = 2): Int {
        val rope = ropeLength.downTo(1).map { Pair(0, 0) }.toMutableList()
        return input
            .asSequence()
            .map { it.split(" ") }
            .flatMap { (direction, amount) ->
                amount.toInt().downTo(1).asSequence().map { direction }
            }
            .map { direction ->
                for ((i, knot) in rope.withIndex()) {
                    if (i == 0) {
                        rope[0] = when (direction) {
                            "R" -> knot.copy(first = knot.first + 1)
                            "U" -> knot.copy(second = knot.second + 1)
                            "L" -> knot.copy(first = knot.first - 1)
                            "D" -> knot.copy(second = knot.second - 1)
                            else -> error("Unexpected direction: $direction")
                        }
                    } else {
                        val head = rope[i - 1]
                        val tail = rope[i]
                        rope[i] = when {
                            head.first == tail.first ->
                                when (val distance = head.second - tail.second) {
                                    -1, 0, 1 -> tail
                                    -2 -> tail.copy(second = tail.second - 1)
                                    2 -> tail.copy(second = tail.second + 1)
                                    else -> error("Unexpected distance: $distance")
                                }

                            head.second == tail.second ->
                                when (val distance = head.first - tail.first) {
                                    -1, 0, 1 -> tail
                                    -2 -> tail.copy(first = tail.first - 1)
                                    2 -> tail.copy(first = tail.first + 1)
                                    else -> error("Unexpected distance: $distance")
                                }

                            else ->
                                when (val xDistance = head.first - tail.first) {
                                    -1, 1 -> when (val yDistance = head.second - tail.second) {
                                        -1, 1 -> tail
                                        -2 -> tail.copy(tail.first + xDistance, tail.second - 1)
                                        2 -> tail.copy(tail.first + xDistance, tail.second + 1)
                                        else -> error("Unexpected distances: $xDistance / $yDistance")
                                    }

                                    -2 -> when (val yDistance = head.second - tail.second) {
                                        -1 -> tail.copy(tail.first - 1, tail.second + yDistance)
                                        1 -> tail.copy(tail.first - 1, tail.second + yDistance)
                                        -2 -> tail.copy(tail.first - 1, tail.second - 1)
                                        2 -> tail.copy(tail.first - 1, tail.second + 1)
                                        else -> error("Unexpected distances: $xDistance / $yDistance")
                                    }

                                    2 -> when (val yDistance = head.second - tail.second) {
                                        -1 -> tail.copy(tail.first + 1, tail.second + yDistance)
                                        1 -> tail.copy(tail.first + 1, tail.second + yDistance)
                                        -2 -> tail.copy(tail.first + 1, tail.second - 1)
                                        2 -> tail.copy(tail.first + 1, tail.second + 1)
                                        else -> error("Unexpected distances: $xDistance / $yDistance")
                                    }

                                    else -> error("Unexpected distance: $xDistance")
                                }
                        }
                    }
                }

                return@map rope.last()
            }
            .toSet()
            .count()
    }

    fun part2(input: List<String>) = part1(input, ropeLength = 10)

    val testInput1 = readStrings("Day09_test1")
    check(part1(testInput1) == 13)

    val input = readStrings("Day09")
    println(part1(input))

    check(part2(testInput1) == 1)
    val testInput2 = readStrings("Day09_test2")
    check(part2(testInput2) == 36)
    println(part2(input))
}
