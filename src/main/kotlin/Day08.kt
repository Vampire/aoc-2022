fun main() {
    data class Tree(
        val height: Int,
        var n: Tree? = null,
        var e: Tree? = null,
        var s: Tree? = null,
        var w: Tree? = null
    )

    fun buildForest(map: List<String>) = map
        .map {
            it
                .toCharArray()
                .map(Char::digitToInt)
                .map(::Tree)
        }
        .map {
            generateSequence(
                it.reduce { w, e ->
                    w.e = e
                    e.w = w
                    e
                },
                Tree::w
            ).toList()
        }
        .reduce { nRow, sRow ->
            nRow.zip(sRow) { n, s ->
                n.s = s
                s.n = n
                s
            }
        }
        .first()
        .let { tree ->
            generateSequence(tree, Tree::e) + generateSequence(tree.w, Tree::w)
        }
        .flatMap { tree ->
            generateSequence(tree, Tree::n) + generateSequence(tree.s, Tree::s)
        }

    fun part1(input: List<String>) = buildForest(input)
        .filter { tree ->
            listOf(
                generateSequence(tree.n, Tree::n),
                generateSequence(tree.e, Tree::e),
                generateSequence(tree.s, Tree::s),
                generateSequence(tree.w, Tree::w)
            ).any { it.all { it.height < tree.height } }
        }
        .count()

    fun part2(input: List<String>) = buildForest(input)
        .map { tree ->
            listOf(
                generateSequence(tree.n, Tree::n) + Tree(Int.MAX_VALUE),
                generateSequence(tree.e, Tree::e) + Tree(Int.MAX_VALUE),
                generateSequence(tree.s, Tree::s) + Tree(Int.MAX_VALUE),
                generateSequence(tree.w, Tree::w) + Tree(Int.MAX_VALUE)
            ).map {
                val i = it.indexOfFirst { it.height >= tree.height }
                when {
                    it.count() == 0 -> 0
                    i == it.count() - 1 -> i
                    else -> i + 1
                }
            }.reduce(Int::times)
        }
        .max()

    val testInput = readStrings("Day08_test")
    check(part1(testInput) == 21)

    val input = readStrings("Day08")
    println(part1(input))

    check(part2(testInput) == 8)
    println(part2(input))
}
