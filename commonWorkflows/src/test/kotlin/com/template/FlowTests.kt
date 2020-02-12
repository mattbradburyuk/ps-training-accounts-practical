package com.template

import com.template.flows.Responder
import com.template.flows.WhoAreYou
import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test

class FlowTests {
    private val network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
        TestCordapp.findCordapp("com.template.contracts"),
        TestCordapp.findCordapp("com.template.flows")
    )))
    private val a = network.createNode()
    private val b = network.createNode()

    init {
        listOf(a, b).forEach {
            it.registerInitiatedFlow(Responder::class.java)
//            it.registerInitiatedFlow(ReceiverWhoAmIResponderFlow::class.java)
        }
    }

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `dummy test`() {


        val aparty = a.info.legalIdentities.single()
        val bparty = b.info.legalIdentities.single()

        val flow1 = WhoAreYou(listOf(bparty))
        val future1 = a.startFlow(flow1)
        network.runNetwork()
        val result1 = future1.getOrThrow()

        println("MB: $result1")


    }
}