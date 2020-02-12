package com.template.testing

import com.template.iot.flows.WhoAreYouIOTResponderFlow
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
    )))
    private val a = network.createNode(
            MockNodeParameters(additionalCordapps = listOf(TestCordapp.findCordapp("com.template.producer.flows"))))
    private val b = network.createNode(
            MockNodeParameters(additionalCordapps = listOf(TestCordapp.findCordapp("com.template.receiver.flows"))))
    private val c = network.createNode(
            MockNodeParameters(additionalCordapps = listOf(TestCordapp.findCordapp("com.template.iot.flows"))))

    init {
        b.registerInitiatedFlow(WhoAreYouReceiverResponderFlow::class.java)
        c.registerInitiatedFlow(WhoAreYouIOTResponderFlow::class.java)
    }



    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `test responder`() {

        val bparty = b.info.legalIdentities.single()

        val flow1 = WhoAreYouProducerInitiatorFlow(listOf(bparty))
        val future1 = a.startFlow(flow1)
        network.runNetwork()
        val result1 = future1.getOrThrow()

        assert( result1 == "Messages: I am the Receiver ")

    }
    @Test
    fun `test iot`() {

        val cparty = c.info.legalIdentities.single()

        val flow1 = WhoAreYouProducerInitiatorFlow(listOf(cparty))
        val future1 = a.startFlow(flow1)
        network.runNetwork()
        val result1 = future1.getOrThrow()

        assert( result1 == "Messages: I am the IOT device ")

    }

    @Test
    fun `test both responder and iot`() {

        val bparty = b.info.legalIdentities.single()
        val cparty = c.info.legalIdentities.single()

        val flow1 = WhoAreYouProducerInitiatorFlow(listOf(bparty,cparty))
        val future1 = a.startFlow(flow1)
        network.runNetwork()
        val result1 = future1.getOrThrow()

        println("MB: result1: $result1")
        assert( result1 == "Messages: I am the Receiver  I am the IOT device ")

    }
}