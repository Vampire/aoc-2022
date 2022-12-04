fun main() {
    data class File(
        val name: String,
        val size: Int
    )

    data class Dir(
        val name: String,
        val parent: Dir? = null,
        val dirs: MutableList<Dir> = mutableListOf(),
        val files: MutableList<File> = mutableListOf()
    ) {
        val size: Int
            get() = dirs.sumOf(Dir::size) + files.sumOf(File::size)

        val descendentDirs: List<Dir>
            get() = dirs.flatMap { it.allDirs }

        val allDirs: List<Dir>
            get() = descendentDirs + this
    }

    fun buildDirTree(cliOutput: List<String>): Dir {
        val root = Dir("/")
        var cur: Dir? = null
        cliOutput.forEach { line ->
            when {
                line == "$ cd /" -> cur = root
                line == "$ cd .." -> cur = cur!!.parent!!
                line.startsWith("$ cd ") -> cur = cur!!.dirs.find { it.name == line.substring(5) }!!
                line == "$ ls" -> Unit
                line.startsWith("dir ") -> cur!!.dirs.add(Dir(line.substring(4), cur))
                else -> line.split(" ", limit = 2).let { (size, name) -> cur!!.files.add(File(name, size.toInt())) }
            }
        }
        return root
    }

    fun part1(input: List<String>) = buildDirTree(input)
        .allDirs
        .map(Dir::size)
        .filter { it <= 100_000 }
        .sum()

    fun part2(input: List<String>): Int {
        val root = buildDirTree(input)
        val freeSpace = 70_000_000 - root.size
        val requiredAdditionalSpace = 30_000_000 - freeSpace
        return root
            .allDirs
            .map(Dir::size)
            .sorted()
            .find { it > requiredAdditionalSpace }!!
    }

    val testInput = readStrings("Day07_test")
    check(part1(testInput) == 95_437)

    val input = readStrings("Day07")
    println(part1(input))

    check(part2(testInput) == 24_933_642)
    println(part2(input))
}
