package org.smalllibs.actor.impl

import org.junit.Test
import org.smalllibs.actor.reference.ActorPathImpl
import kotlin.test.assertEquals

class ActorPathTest {

    @Test
    fun shouldChildHasCorrectParent() {
        val root = ActorPathImpl("root")
        val child = root.newChild("child")

        assertEquals(child.parent, root)
    }

}