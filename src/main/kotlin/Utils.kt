import java.math.BigInteger
import java.security.MessageDigest

fun readStrings(name: String) = object {}::class.java.getResource("$name.txt")!!
    .readText()
    .trim()
    .lines()

fun readInts(name: String) = readStrings(name).map { it.toInt() }

fun String.md5() = toByteArray()
    .let(MessageDigest.getInstance("MD5")::digest)
    .let { BigInteger(1, it) }
    .toString(16)
    .padStart(32, '0')
