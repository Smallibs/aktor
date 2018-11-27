package org.smalllibs.actor.core

import org.junit.Test
import kotlin.test.assertEquals

class ActorAddressTest {

    @Test
    fun shouldChildHasCorrectParent() {
        val root = ActorAddressImpl("root")
        val child = root.newChild("child")

        assertEquals(root parentOf child, true)
    }

    @Test
    fun shouldParentHasCorrectChild() {
        val root = ActorAddressImpl("root")
        val child = root.newChild("child")

        assertEquals(child childOf root, true)
    }

}