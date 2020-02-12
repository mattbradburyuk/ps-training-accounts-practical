package com.template.receiver.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.common.flows.WhoAreYouInitiatorFlow
import com.template.common.flows.WhoAreYouResponderFlow

import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.utilities.unwrap



//@InitiatedBy(WhoAreYouInitiatorFlow::class)
//class WhoAreYouReceiverResponderFlow(otherPartySession: FlowSession): WhoAreYouResponderFlow(otherPartySession){

//@InitiatedBy(WhoAreYouProducerInitiatorFlow::class)
//class WhoAreYouReceiverResponderFlow(val otherPartySession: FlowSession): FlowLogic<Unit>(){
//
//    @Suspendable
//    override fun call(){
//
//        val str = "I am the Receiver"
//        val received = otherPartySession.receive<String>().unwrap { it }
//        otherPartySession.send(str)
//    }
//}
