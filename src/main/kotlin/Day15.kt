import kotlin.math.abs

typealias Coordinates = Pair<Int, Int>

fun main() {
    fun Coordinates.distance(other: Coordinates) =
        abs(other.first - first) + abs(other.second - second)

    data class SensorResult(val sensor: Coordinates, val beacon: Coordinates) {
        val distance = sensor.distance(beacon)
    }

    val inputPattern = """Sensor at x=(?<sensorX>-?\d++), y=(?<sensorY>-?\d++): closest beacon is at x=(?<beaconX>-?\d++), y=(?<beaconY>-?\d++)""".toPattern()

    fun parseSensorResults(input: List<String>) = input
        .map {
            val matcher = inputPattern.matcher(it)
            check(matcher.matches())
            SensorResult(
                Coordinates(
                    matcher.group("sensorX").toInt(),
                    matcher.group("sensorY").toInt()
                ),
                Coordinates(
                    matcher.group("beaconX").toInt(),
                    matcher.group("beaconY").toInt()
                )
            )
        }

    fun part1(input: List<String>, rowToInvestigate: Int): Int {
        val sensorResults = parseSensorResults(input)
        val minX = sensorResults.minOf { it.sensor.first - it.distance }
        val maxX = sensorResults.maxOf { it.sensor.first + it.distance }
        return (minX..maxX).count { x ->
            val coordinates = Coordinates(x, rowToInvestigate)
            sensorResults.none { (it.sensor == coordinates) || (it.beacon == coordinates) }
                    && sensorResults.any { it.sensor.distance(coordinates) <= it.distance }
        }
    }

    fun part2(input: List<String>, maxCoordinate: Int): Long {
        val sensorResults = parseSensorResults(input)
        return sensorResults
            .asSequence()
            .flatMap { sensorResult ->
                val candidateDistance = sensorResult.distance + 1
                (0..candidateDistance)
                    .asSequence()
                    .associateWith { candidateDistance - it }
                    .flatMap {
                        val sensorX = sensorResult.sensor.first
                        val sensorY = sensorResult.sensor.second
                        sequenceOf(
                            Coordinates(sensorX + it.key, sensorY + it.value),
                            Coordinates(sensorX + it.key, sensorY + -it.value),
                            Coordinates(sensorX + -it.key, sensorY + it.value),
                            Coordinates(sensorX + -it.key, sensorY + -it.value)
                        )
                    }
            }
            .filter {
                sequenceOf(it.first, it.second).all { coordinate ->
                    coordinate in 0..maxCoordinate
                }
            }
            .find { coordinates ->
                sensorResults.none { it.sensor.distance(coordinates) <= it.distance }
            }!!
            .let { (it.first * 4_000_000L) + it.second }
    }

    val testInput = readStrings("Day15_test")
    check(part1(testInput, rowToInvestigate = 10) == 26)

    val input = readStrings("Day15")
    println(part1(input, rowToInvestigate = 2_000_000))

    check(part2(testInput, maxCoordinate = 20) == 56_000_011L)
    println(part2(input, maxCoordinate = 4_000_000))
}
