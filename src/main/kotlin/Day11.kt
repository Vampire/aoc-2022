fun main() {
    data class Monkey(
        val id: Int,
        val items: MutableList<Long> = mutableListOf(),
        var operation: (Long) -> Long = { it },
        var testDivisor: Int = -1,
        var trueTarget: Int = -1,
        var falseTarget: Int = -1,
        var activity: Long = 0
    ) {
        fun test(dividend: Long) = dividend % testDivisor == 0L
    }

    fun part1(input: List<String>, reliefFactor: Int = 3, rounds: Int = 20): Long {
        val notebook = input.filter { it.isNotBlank() }.iterator()
        val monkeys = mutableMapOf<Int, Monkey>()
        for (monkeyId in notebook) {
            check(monkeyId.endsWith(":"))
            val monkey = Monkey(monkeyId.dropLast(1).substringAfter("Monkey ").toInt())
            monkeys[monkey.id] = monkey

            val startingItems = notebook.next()
            monkey.items.addAll(
                startingItems
                    .substringAfter("  Starting items: ")
                    .split(", ")
                    .map(String::toLong)
                    .toMutableList()
            )

            val operation = notebook.next()
            val (operator, operand) = operation
                .substringAfter("  Operation: new = old ")
                .split(" ")
            monkey.operation = when (operator) {
                "+" -> if (operand == "old") ({ it + it }) else (operand.toLong().let { intOperand -> { it + intOperand } })
                "*" -> if (operand == "old") ({ it * it }) else (operand.toLong().let { intOperand -> { it * intOperand } })
                else -> error("Unexpected operator $operator")
            }

            val testDivisor = notebook.next()
            monkey.testDivisor = testDivisor
                .substringAfter("  Test: divisible by ")
                .toInt()

            val trueTarget = notebook.next()
            monkey.trueTarget = trueTarget.substringAfter("    If true: throw to monkey ").toInt()

            val falseTarget = notebook.next()
            monkey.falseTarget = falseTarget.substringAfter("    If false: throw to monkey ").toInt()
        }

        val sanitizingDivisor = if (reliefFactor == 1) monkeys
            .values
            .map(Monkey::testDivisor)
            .reduce(Int::times) else Int.MAX_VALUE

        for (round in 1..rounds) {
            for (monkey in monkeys.values) {
                for (item in monkey.items) {
                    val newItem = monkey.operation(item) / reliefFactor % sanitizingDivisor
                    monkeys[if (monkey.test(newItem)) monkey.trueTarget else monkey.falseTarget]!!.items.add(newItem)
                }
                monkey.activity += monkey.items.size
                monkey.items.clear()
            }
        }

        return monkeys
            .values
            .map(Monkey::activity)
            .sortedDescending()
            .take(2)
            .reduce(Long::times)
    }

    fun part2(input: List<String>) = part1(input, reliefFactor = 1, rounds = 10_000)

    val testInput = readStrings("Day11_test")
    check(part1(testInput) == 10_605L)

    val input = readStrings("Day11")
    println(part1(input))

    check(part2(testInput) == 2_713_310_158)
    println(part2(input))
}
