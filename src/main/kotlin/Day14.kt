import kotlin.math.max

typealias Node = Pair<Int, Int>

fun main() {
    fun part1(input: List<String>, withFloor: Boolean = false): Int {
        val structures = input
            .map { structure ->
                structure
                    .split(" -> ")
                    .map { node ->
                        node
                            .split(",")
                            .map(String::toInt)
                            .let { (x, y) -> Node(x, y) }
                    }
            }
        val maxX = structures.maxOf { structure -> structure.maxOf(Node::first) }
        val minX = structures.minOf { structure -> structure.minOf(Node::first) }
        val maxY = structures.maxOf { structure -> structure.maxOf(Node::second) }
        val floorY = maxY + 2
        val cave = arrayOf(
            *((0..max(maxX, 500 + (floorY + 1))).map {
                booleanArrayOf(
                    *((0..floorY).map {
                        false
                    }.toBooleanArray())
                )
            }.toTypedArray())
        )

        structures
            .toMutableList()
            .apply {
                add(listOf(Node(500 - (floorY + 1), floorY), Node(500 + (floorY + 1), floorY)))
            }
            .forEach { structure ->
                var lastNode = structure.first()
                cave[lastNode.first][lastNode.second] = true
                for (node in structure.drop(1)) {
                    when {
                        lastNode.first == node.first -> listOf(lastNode, node)
                            .map(Node::second)
                            .sorted()
                            .also { (from, to) ->
                                (from..to).forEach {
                                    cave[node.first][it] = true
                                }
                            }

                        lastNode.second == node.second -> listOf(lastNode, node)
                            .map(Node::first)
                            .sorted()
                            .also { (from, to) ->
                                (from..to).forEach {
                                    cave[it][node.second] = true
                                }
                            }

                        else -> error("Broken structure $lastNode -> $node")
                    }
                    lastNode = node
                }
            }

        var sandAmount = -1
        while (true) {
            sandAmount++
            var sandX = 500
            var sandY = 0
            while (true) {
                if (withFloor && (sandX == 500) && (sandY == 0) && cave[sandX][sandY]) {
                    return sandAmount
                }

                when {
                    !cave[sandX][sandY + 1] -> sandY++

                    !cave[sandX - 1][sandY + 1] -> {
                        sandX--
                        sandY++
                    }

                    !cave[sandX + 1][sandY + 1] -> {
                        sandX++
                        sandY++
                    }

                    else -> {
                        cave[sandX][sandY] = true
                        break
                    }
                }

                if (!withFloor && ((sandX !in (minX..maxX)) || (sandY >= maxY))) {
                    return sandAmount
                }
            }
        }
    }

    fun part2(input: List<String>) = part1(input, withFloor = true)

    val testInput = readStrings("Day14_test")
    check(part1(testInput) == 24)

    val input = readStrings("Day14")
    println(part1(input))

    check(part2(testInput) == 93)
    println(part2(input))
}
