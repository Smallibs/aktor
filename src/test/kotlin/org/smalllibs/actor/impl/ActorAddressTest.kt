package org.smalllibs.actor.impl

import org.junit.Test
import kotlin.test.assertEquals

class ActorAddressTest {

    @Test
    fun shouldChildHasCorrectParent() {
        val root = ActorAddressImpl<String>(ActorPathImpl("root"))
        val child = ActorAddressImpl<String>(root.path.newChild("child"))

        assertEquals(root parentOf child, true)
    }

    @Test
    fun shouldParentHasCorrectChild() {
        val root = ActorAddressImpl<String>(ActorPathImpl("root"))
        val child = ActorAddressImpl<String>(root.path.newChild("child"))

        assertEquals(child childOf root, true)
    }

}