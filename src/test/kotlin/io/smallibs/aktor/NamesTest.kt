package io.smallibs.aktor

import org.junit.Test
import kotlin.math.roundToInt
import kotlin.test.assertEquals

class NamesTest {

    @Test
    fun shouldPickNames() {
        with(Names) {
            val array = arrayOf(1, 2)
            val set = mutableSetOf<Int>()
            repeat(1000) { // it's very unlikely to have 1000 time the same result
                set.add(array.pickRandom())
            }
            assertEquals(array.size, set.size)
        }
    }

    @Test
    fun shouldGenerateUniqueNames() {
        // left hand size: 100
        // right hand size: 234
        val testNb = (100 * 234 * 1.1).roundToInt() // ensure duplication

        val set = mutableSetOf<String>()
        repeat(testNb) {
            set.add(Names.generate())
        }

        println(set.joinToString(separator = "\n", limit = 10))

        assertEquals(testNb, set.size)
    }

}