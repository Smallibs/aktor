package org.smalllibs.actor.impl

import org.junit.Test
import org.smalllibs.actor.reference.ActorAddressImpl
import org.smalllibs.actor.reference.ActorPathImpl
import kotlin.test.assertEquals

class ActorAddressTest {

    @Test
    fun shouldChildHasCorrectParent() {
        val root = ActorAddressImpl(ActorPathImpl("root"))
        val child = ActorAddressImpl(root.path.newChild("child"))

        assertEquals(root parentOf child, true)
    }

    @Test
    fun shouldParentHasCorrectChild() {
        val root = ActorAddressImpl(ActorPathImpl("root"))
        val child = ActorAddressImpl(root.path.newChild("child"))

        assertEquals(child childOf root, true)
    }

}