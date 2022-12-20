import kotlin.math.max

fun main() {
    data class Valve(
        val name: String,
        val flowRate: Int,
        val reachableValveNames: List<String>
    ) {
        val reachableValves = mutableListOf<Valve>()
    }

    val inputPattern = """Valve (?<valveName>\w++) has flow rate=(?<flowRate>\d++); (?:tunnels lead to valves|tunnel leads to valve) (?<reachableValves>.*)""".toPattern()

    fun parseValveNetwork(input: List<String>) = input
        .map {
            val matcher = inputPattern.matcher(it)
            check(matcher.matches())
            Valve(
                name = matcher.group("valveName"),
                flowRate = matcher.group("flowRate").toInt(),
                reachableValveNames = matcher.group("reachableValves").split(", ")
            )
        }
        .also { valves ->
            valves.forEach { valve ->
                valve
                    .reachableValveNames
                    .map { valveName -> valves.find { it.name == valveName }!! }
                    .forEach(valve.reachableValves::add)
            }
        }

    data class ValveWithPath(val valve: Valve) {
        val path = mutableListOf<Valve>()
        val distance get() = path.size
    }

    val shortestPathsCache = mutableMapOf<Valve, Map<Valve, ValveWithPath>>()

    fun calculateShortestPaths(valves: List<Valve>, start: Valve): Map<Valve, ValveWithPath> {
        var current: ValveWithPath? = null
        val visited = mutableSetOf<ValveWithPath>()
        val unvisited = valves.map {
            ValveWithPath(it).also { result ->
                if (it == start) {
                    current = result
                } else {
                    result.path.addAll(valves)
                }
            }
        }.toMutableSet()
        while (unvisited.isNotEmpty()) {
            current!!
                .valve
                .reachableValves
                .mapNotNull { valve -> unvisited.find { it.valve.name == valve.name } }
                .forEach {
                    if (current!!.distance + 1 < it.distance) {
                        it.path.clear()
                        it.path.addAll(current!!.path)
                        it.path.add(it.valve)
                    }
                }
            visited.add(current!!)
            unvisited.remove(current)
            current = unvisited.minByOrNull(ValveWithPath::distance)
        }
        return visited.associateBy { it.valve }
    }

    fun getMaxSteamRecursive(
        valves: List<Valve>,
        start: Valve,
        closedValves: Set<Valve>,
        remainingTime: Int,
        releasedSteam: Int,
        maxSteam: Array<Int>
    ): Int {
        if ((closedValves.sumOf { remainingTime * it.flowRate } + releasedSteam + (remainingTime * start.flowRate)) <= maxSteam[0]) {
            return 0
        }
        return shortestPathsCache
            .computeIfAbsent(start) { key -> calculateShortestPaths(valves, key) }
            .values
            .asSequence()
            .filter { it.valve in closedValves }
            .filter { (remainingTime - (it.distance + 1)) > 0 }
            .let { possibleNextValves ->
                possibleNextValves.maxOfOrNull {
                    getMaxSteamRecursive(
                        valves = valves,
                        start = it.valve,
                        closedValves = closedValves.filterNot(it.valve::equals).toSet(),
                        remainingTime = remainingTime - (it.distance + 1),
                        releasedSteam = releasedSteam + (remainingTime * start.flowRate),
                        maxSteam = maxSteam
                    )
                } ?: (releasedSteam + (remainingTime * start.flowRate))
            }
            .also { maxSteam[0] = max(it, maxSteam[0]) }
    }

    fun getMaxSteamRecursiveWithElephant(
        valves: List<Valve>,
        currentValve: Valve?,
        currentValveElephant: Valve?,
        currentTarget: Valve?,
        currentTargetElephant: Valve?,
        openValves: Set<Valve>,
        closedValves: Set<Valve>,
        remainingTime: Int,
        releasedSteam: Int,
        maxSteam: Array<Int>
    ): Int {
        check(remainingTime >= 0) {
            "Algorithm error"
        }
        if ((valves.sumOf { remainingTime * it.flowRate } + releasedSteam) <= maxSteam[0]) {
            return 0
        }
        val newReleasedSteam = releasedSteam + openValves.sumOf { it.flowRate }
        val shortestPaths = if (currentValve == null) mapOf() else shortestPathsCache.computeIfAbsent(currentValve) { key ->
            calculateShortestPaths(valves, key)
        }
        val shortestPathsElephant = if (currentValveElephant == null) mapOf() else shortestPathsCache.computeIfAbsent(currentValveElephant) { key ->
            calculateShortestPaths(valves, key)
        }
        return if ((currentTarget != currentValve) && (currentTargetElephant != currentValveElephant)) {
            getMaxSteamRecursiveWithElephant(
                valves = valves,
                currentValve = shortestPaths[currentTarget]!!.path.first(),
                currentValveElephant = shortestPathsElephant[currentTargetElephant]!!.path.first(),
                currentTarget = currentTarget,
                currentTargetElephant = currentTargetElephant,
                openValves = openValves,
                closedValves = closedValves,
                remainingTime = remainingTime - 1,
                releasedSteam = newReleasedSteam,
                maxSteam = maxSteam
            )
        } else if ((currentTarget == currentValve) && (currentTargetElephant != currentValveElephant)) {
            if (currentValve in openValves) {
                shortestPaths
                    .values
                    .asSequence()
                    .filter { it.valve in closedValves }
                    .filter { (remainingTime - (it.distance + 1)) > 0 }
                    .let { possibleNextValves ->
                        possibleNextValves.maxOfOrNull { nextValve ->
                            getMaxSteamRecursiveWithElephant(
                                valves = valves,
                                currentValve = nextValve.path.first(),
                                currentValveElephant = shortestPathsElephant[currentTargetElephant]!!.path.first(),
                                currentTarget = nextValve.valve,
                                currentTargetElephant = currentTargetElephant,
                                openValves = openValves,
                                closedValves = closedValves.filter { it != nextValve.valve }.toSet(),
                                remainingTime = remainingTime - 1,
                                releasedSteam = newReleasedSteam,
                                maxSteam = maxSteam
                            )
                        } ?: getMaxSteamRecursiveWithElephant(
                            valves = valves,
                            currentValve = null,
                            currentValveElephant = shortestPathsElephant[currentTargetElephant]!!.path.first(),
                            currentTarget = null,
                            currentTargetElephant = currentTargetElephant,
                            openValves = openValves,
                            closedValves = closedValves,
                            remainingTime = remainingTime - 1,
                            releasedSteam = newReleasedSteam,
                            maxSteam = maxSteam
                        )
                    }
            } else {
                val newOpenValves = openValves + currentValve
                getMaxSteamRecursiveWithElephant(
                    valves = valves,
                    currentValve = currentValve,
                    currentValveElephant = shortestPathsElephant[currentTargetElephant]!!.path.first(),
                    currentTarget = currentTarget,
                    currentTargetElephant = currentTargetElephant,
                    openValves = newOpenValves.filterNotNull().toSet(),
                    closedValves = closedValves,
                    remainingTime = remainingTime - 1,
                    releasedSteam = newReleasedSteam,
                    maxSteam = maxSteam
                )
            }
        } else if ((currentTarget != currentValve) && (currentTargetElephant == currentValveElephant)) {
            if (currentValveElephant in openValves) {
                shortestPathsElephant
                    .values
                    .asSequence()
                    .filter { it.valve in closedValves }
                    .filter { (remainingTime - (it.distance + 1)) > 0 }
                    .let { possibleNextValves ->
                        possibleNextValves.maxOfOrNull { nextValve ->
                            getMaxSteamRecursiveWithElephant(
                                valves = valves,
                                currentValve = shortestPaths[currentTarget]!!.path.first(),
                                currentValveElephant = nextValve.path.first(),
                                currentTarget = currentTarget,
                                currentTargetElephant = nextValve.valve,
                                openValves = openValves,
                                closedValves = closedValves.filter { it != nextValve.valve }.toSet(),
                                remainingTime = remainingTime - 1,
                                releasedSteam = newReleasedSteam,
                                maxSteam = maxSteam
                            )
                        } ?: getMaxSteamRecursiveWithElephant(
                            valves = valves,
                            currentValve = shortestPaths[currentTarget]!!.path.first(),
                            currentValveElephant = null,
                            currentTarget = currentTarget,
                            currentTargetElephant = null,
                            openValves = openValves,
                            closedValves = closedValves,
                            remainingTime = remainingTime - 1,
                            releasedSteam = newReleasedSteam,
                            maxSteam = maxSteam
                        )
                    }
            } else {
                val newOpenValves = openValves + currentValveElephant
                getMaxSteamRecursiveWithElephant(
                    valves = valves,
                    currentValve = shortestPaths[currentTarget]!!.path.first(),
                    currentValveElephant = currentValveElephant,
                    currentTarget = currentTarget,
                    currentTargetElephant = currentTargetElephant,
                    openValves = newOpenValves.filterNotNull().toSet(),
                    closedValves = closedValves,
                    remainingTime = remainingTime - 1,
                    releasedSteam = newReleasedSteam,
                    maxSteam = maxSteam
                )
            }
        } else {
            check((currentTarget == currentValve) && (currentTargetElephant == currentValveElephant)) {
                "Algorithm error"
            }
            if ((currentValve == null) && (currentValveElephant == null)) {
                return (newReleasedSteam + ((remainingTime - 1) * openValves.sumOf { it.flowRate }))
            } else if (currentValve in openValves) {
                if (currentValveElephant in openValves) {
                    shortestPaths
                        .values
                        .asSequence()
                        .filter { it.valve in closedValves }
                        .filter { (remainingTime - (it.distance + 1)) > 0 }
                        .let { possibleNextValves ->
                            possibleNextValves.maxOfOrNull { nextValve ->
                                shortestPathsElephant
                                    .values
                                    .asSequence()
                                    .filter { (it.valve in closedValves) && (it.valve != nextValve.valve) }
                                    .filter { (remainingTime - (it.distance + 1)) > 0 }
                                    .let { possibleNextValvesElephant ->
                                        possibleNextValvesElephant.maxOfOrNull { nextValveElephant ->
                                            getMaxSteamRecursiveWithElephant(
                                                valves = valves,
                                                currentValve = nextValve.path.first(),
                                                currentValveElephant = nextValveElephant.path.first(),
                                                currentTarget = nextValve.valve,
                                                currentTargetElephant = nextValveElephant.valve,
                                                openValves = openValves,
                                                closedValves = closedValves.filter { (it != nextValve.valve) && (it != nextValveElephant.valve) }.toSet(),
                                                remainingTime = remainingTime - 1,
                                                releasedSteam = newReleasedSteam,
                                                maxSteam = maxSteam
                                            )
                                        } ?: getMaxSteamRecursiveWithElephant(
                                            valves = valves,
                                            currentValve = nextValve.path.first(),
                                            currentValveElephant = null,
                                            currentTarget = nextValve.valve,
                                            currentTargetElephant = null,
                                            openValves = openValves,
                                            closedValves = closedValves.filter { it != nextValve.valve }.toSet(),
                                            remainingTime = remainingTime - 1,
                                            releasedSteam = newReleasedSteam,
                                            maxSteam = maxSteam
                                        )
                                    }
                            } ?: shortestPathsElephant
                                .values
                                .asSequence()
                                .filter { it.valve in closedValves }
                                .filter { (remainingTime - (it.distance + 1)) > 0 }
                                .let { possibleNextValvesElephant ->
                                    possibleNextValvesElephant.maxOfOrNull { nextValveElephant ->
                                        getMaxSteamRecursiveWithElephant(
                                            valves = valves,
                                            currentValve = null,
                                            currentValveElephant = nextValveElephant.path.first(),
                                            currentTarget = null,
                                            currentTargetElephant = nextValveElephant.valve,
                                            openValves = openValves,
                                            closedValves = closedValves.filter { it != nextValveElephant.valve }.toSet(),
                                            remainingTime = remainingTime - 1,
                                            releasedSteam = newReleasedSteam,
                                            maxSteam = maxSteam
                                        )
                                    } ?: ((remainingTime - 1) * openValves.sumOf { it.flowRate })
                                }
                        }
                } else {
                    val newOpenValves = openValves + currentValveElephant
                    shortestPaths
                        .values
                        .asSequence()
                        .filter { it.valve in closedValves }
                        .filter { (remainingTime - (it.distance + 1)) > 0 }
                        .let { possibleNextValves ->
                            possibleNextValves.maxOfOrNull { nextValve ->
                                getMaxSteamRecursiveWithElephant(
                                    valves = valves,
                                    currentValve = nextValve.path.first(),
                                    currentValveElephant = currentValveElephant,
                                    currentTarget = nextValve.valve,
                                    currentTargetElephant = currentTargetElephant,
                                    openValves = newOpenValves.filterNotNull().toSet(),
                                    closedValves = closedValves.filter { it != nextValve.valve }.toSet(),
                                    remainingTime = remainingTime - 1,
                                    releasedSteam = newReleasedSteam,
                                    maxSteam = maxSteam
                                )
                            } ?: getMaxSteamRecursiveWithElephant(
                                valves = valves,
                                currentValve = null,
                                currentValveElephant = currentValveElephant,
                                currentTarget = null,
                                currentTargetElephant = currentTargetElephant,
                                openValves = newOpenValves.filterNotNull().toSet(),
                                closedValves = closedValves,
                                remainingTime = remainingTime - 1,
                                releasedSteam = newReleasedSteam,
                                maxSteam = maxSteam
                            )
                        }
                }
            } else {
                if (currentValveElephant in openValves) {
                    val newOpenValves = openValves + currentValve
                    shortestPathsElephant
                        .values
                        .asSequence()
                        .filter { it.valve in closedValves }
                        .filter { (remainingTime - (it.distance + 1)) > 0 }
                        .let { possibleNextValves ->
                            possibleNextValves.maxOfOrNull { nextValve ->
                                getMaxSteamRecursiveWithElephant(
                                    valves = valves,
                                    currentValve = currentValve,
                                    currentValveElephant = nextValve.path.first(),
                                    currentTarget = currentTarget,
                                    currentTargetElephant = nextValve.valve,
                                    openValves = newOpenValves.filterNotNull().toSet(),
                                    closedValves = closedValves.filter { it != nextValve.valve }.toSet(),
                                    remainingTime = remainingTime - 1,
                                    releasedSteam = newReleasedSteam,
                                    maxSteam = maxSteam
                                )
                            } ?: getMaxSteamRecursiveWithElephant(
                                valves = valves,
                                currentValve = currentValve,
                                currentValveElephant = null,
                                currentTarget = currentTarget,
                                currentTargetElephant = null,
                                openValves = newOpenValves.filterNotNull().toSet(),
                                closedValves = closedValves,
                                remainingTime = remainingTime - 1,
                                releasedSteam = newReleasedSteam,
                                maxSteam = maxSteam
                            )
                        }
                } else {
                    val newOpenValves = openValves + currentValve + currentValveElephant
                    getMaxSteamRecursiveWithElephant(
                        valves = valves,
                        currentValve = currentValve,
                        currentValveElephant = currentValveElephant,
                        currentTarget = currentTarget,
                        currentTargetElephant = currentTargetElephant,
                        openValves = newOpenValves.filterNotNull().toSet(),
                        closedValves = closedValves,
                        remainingTime = remainingTime - 1,
                        releasedSteam = newReleasedSteam,
                        maxSteam = maxSteam
                    )
                }
            }
        }
            .also { maxSteam[0] = max(it, maxSteam[0]) }
    }

    fun part1(input: List<String>) = parseValveNetwork(input)
        .let { valves ->
            getMaxSteamRecursive(
                valves = valves,
                start = valves.find { it.name == "AA" }!!,
                closedValves = valves.filter { it.flowRate != 0 }.toSet(),
                remainingTime = 30,
                releasedSteam = 0,
                maxSteam = arrayOf(0)
            )
        }

    fun part2(input: List<String>) = parseValveNetwork(input)
        .let { valves ->
            val aa = valves.find { it.name == "AA" }!!
            check(aa.flowRate == 0) {
                "flowRate of AA should be 0 but was ${aa.flowRate}"
            }
            getMaxSteamRecursiveWithElephant(
                valves = valves,
                currentValve = aa,
                currentValveElephant = aa,
                currentTarget = aa,
                currentTargetElephant = aa,
                openValves = setOf(aa),
                closedValves = valves.filter { it.flowRate != 0 }.toSet(),
                remainingTime = 26,
                releasedSteam = 0,
                maxSteam = arrayOf(0)
            )
        }

    val testInput = readStrings("Day16_test")
    check(part1(testInput) == 1_651)

    val input = readStrings("Day16")
    println(part1(input))

    check(part2(testInput) == 1_707)
    println(part2(input))
}
