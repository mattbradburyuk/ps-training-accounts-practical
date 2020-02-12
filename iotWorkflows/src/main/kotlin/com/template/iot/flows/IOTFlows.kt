package com.template.iot.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.common.flows.WhoAreYouInitiatorFlow
import com.template.common.flows.WhoAreYouResponderFlow
import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********

@InitiatedBy(WhoAreYouInitiatorFlow::class)
class WhoAreYouIOTResponderFlow(otherPartySession: FlowSession): WhoAreYouResponderFlow(otherPartySession){

    @Suspendable
    override fun call(){
        logger.info("MB: WhoAreYouReceiverResponderFlow called")
        logger.info("MB: test someOtherFunction(): ${someOtherFunction()}")
        val str = "I am the IOT device"
        val received = otherPartySession.receive<String>().unwrap { it }
        logger.info("MB: received: $received")
        otherPartySession.send(str)
    }
}