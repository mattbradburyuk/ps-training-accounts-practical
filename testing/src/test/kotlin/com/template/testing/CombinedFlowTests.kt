package com.template.testing

import com.template.producer.flows.WhoAreYouProducerInitiatorFlow
import com.template.receiver.flows.WhoAreYouReceiverResponderFlow


import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test

class CombinedFlowTests {
    private val network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
            TestCordapp.findCordapp("com.template.contracts"),
            TestCordapp.findCordapp("com.template.common.flows")
//            TestCordapp.findCordapp("com.template.producer.flows"),
//            TestCordapp.findCordapp("com.template.receiver.flows")

    )))
    private val a = network.createNode(
            MockNodeParameters(additionalCordapps = listOf(TestCordapp.findCordapp("com.template.producer.flows"))))
    private val b = network.createNode(
            MockNodeParameters(additionalCordapps = listOf(TestCordapp.findCordapp("com.template.receiver.flows"))))


    init {
        b.registerInitiatedFlow(WhoAreYouReceiverResponderFlow::class.java)
    }



    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `dummy test`() {


        val aparty = a.info.legalIdentities.single()
        val bparty = b.info.legalIdentities.single()

        val flow1 = WhoAreYouProducerInitiatorFlow(listOf(bparty))
        val future1 = a.startFlow(flow1)
        network.runNetwork()
        val result1 = future1.getOrThrow()

        println("MB: $result1")

        assert( result1 == "Messages: I am the Receiver ")

    }
}