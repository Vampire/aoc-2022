import kotlin.math.min

typealias Distance = Int

fun main() {
    data class Node(val x: Int, val y: Int) {
        var height: Int? = null
        var n: Node? = null
        var e: Node? = null
        var s: Node? = null
        var w: Node? = null
        var distance: Distance = Distance.MAX_VALUE
    }

    fun Char.toHeight() = when (this) {
        'S' -> 0
        'E' -> 'z'.code - 'a'.code
        else -> code - 'a'.code
    }

    fun buildGraph(input: List<String>): Triple<Array<Array<Node>>, Node, Node> {
        val nodes = Array(input.size) { y -> Array(input[y].length) { x -> Node(x, y) } }
        var start: Node? = null
        var end: Node? = null

        for (y in input.indices) {
            val line = input[y]
            for (x in line.indices) {
                val char = line[x]
                val node = nodes[y][x]
                node.height = char.toHeight()
                when (char) {
                    'S' -> start = node
                    'E' -> end = node
                }
            }
        }

        for (y in nodes.indices) {
            val line = nodes[y]
            for (x in line.indices) {
                val node = line[x]
                if ((x > 0) && (nodes[y][x - 1].height!! <= (node.height!! + 1))) {
                    node.w = nodes[y][x - 1]
                }
                if ((x < (line.size - 1)) && (nodes[y][x + 1].height!! <= (node.height!! + 1))) {
                    node.e = nodes[y][x + 1]
                }
                if ((y > 0) && (nodes[y - 1][x].height!! <= (node.height!! + 1))) {
                    node.n = nodes[y - 1][x]
                }
                if ((y < (nodes.size - 1)) && (nodes[y + 1][x].height!! <= (node.height!! + 1))) {
                    node.s = nodes[y + 1][x]
                }
            }
        }
        return Triple(nodes, start!!, end!!)
    }

    fun findShortestPath(
        nodes: Array<Array<Node>>,
        start: Node,
        end: Node,
        shortestPath: Distance = Distance.MAX_VALUE
    ): Distance? {
        val visited = mutableSetOf<Node>()
        val unvisited = nodes.flatten().toMutableSet()
        unvisited.forEach { it.distance = Distance.MAX_VALUE }
        start.distance = 0
        var current = start
        while (current.distance != Distance.MAX_VALUE) {
            listOfNotNull(current.n, current.e, current.s, current.w)
                .intersect(unvisited)
                .forEach {
                    it.distance = min(current.distance + 1, it.distance)
                    check(it.distance >= 0) {
                        "Algorithm error"
                    }
                }
            visited.add(current)
            unvisited.remove(current)
            if (current == end) {
                return end.distance
            } else if (
                visited
                    .filter {
                        listOfNotNull(it.n, it.e, it.s, it.w)
                            .intersect(unvisited)
                            .isNotEmpty()
                    }
                    .all { it.distance >= shortestPath }
            ) {
                return shortestPath
            } else {
                current = unvisited.minBy(Node::distance)
            }
        }
        return null
    }

    fun part1(input: List<String>): Distance {
        val (nodes, start, end) = buildGraph(input)
        return findShortestPath(nodes, start, end)!!
    }

    fun part2(input: List<String>): Distance {
        val (nodes, _, end) = buildGraph(input)
        return nodes
            .flatten()
            .filter { it.height == 0 }
            .fold(Distance.MAX_VALUE) { shortestPath, node ->
                findShortestPath(nodes, node, end, shortestPath) ?: shortestPath
            }
    }

    val testInput = readStrings("Day12_test")
    check(part1(testInput) == 31)

    val input = readStrings("Day12")
    println(part1(input))

    check(part2(testInput) == 29)
    println(part2(input))
}
