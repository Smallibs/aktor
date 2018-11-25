package org.smalllibs.magnet.impl

import org.junit.Test
import kotlin.test.assertEquals

class ActorPathTest {

    @Test
    fun shouldChildHasCorrectParent() {
        val root = ActorPathImpl("root")
        val child = root.newChild("child")

        assertEquals(child.parent(), root)
    }

}