package io.smallibs.aktor.core

import io.smallibs.aktor.core.ActorAddressImpl
import kotlin.test.Test
import kotlin.test.assertTrue

class ActorAddressTest {

    @Test
    fun shouldChildHasCorrectParent() {
        val root = ActorAddressImpl("root")
        val child = root.newChild("child")

        assertTrue { root parentOf child }
    }

    @Test
    fun shouldParentHasCorrectChild() {
        val root = ActorAddressImpl("root")
        val child = root.newChild("child")

        assertTrue { child childOf root }
    }

}