package com.template.common.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        // Initiator flow logic goes here.
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
    }
}

//@InitiatingFlow
//@StartableByRPC
//abstract class WhoAreYouInitiatorFlow(open val parties: List<Party>): FlowLogic<String>()
//
////@InitiatedBy(WhoAreYouInitiatorFlow::class)
//abstract class WhoAreYouResponderFlow(val otherPartySession: FlowSession): FlowLogic<Unit>()


@InitiatingFlow
@StartableByRPC
class WhoAreYouProducerInitiatorFlow(val parties: List<Party>): FlowLogic<String>(){

    @Suspendable
    override fun call(): String {
        logger.info("MB: WhoAreYouProducerInitiatorFlow called")
        val sessions = parties.map { initiateFlow(it) }
        val unsafeResults = sessions.map {it.sendAndReceive<String>("Who are you?")}
        val results = unsafeResults.map { usd -> usd.unwrap {it}  }
        var str = "Messages:"
        for (r in results) str = "$str $r "
        return str
    }
}

//@InitiatedBy(WhoAreYouProducerInitiatorFlow::class)
//class WhoAreYouReceiverResponderFlow(val otherPartySession: FlowSession): FlowLogic<Unit>(){
//
//    @Suspendable
//    override fun call(){
//        logger.info("MB: WhoAreYouReceiverResponderFlow called")
//        val str = "I am the Receiver"
//        val received = otherPartySession.receive<String>().unwrap { it }
//        logger.info("MB: received: $received")
//        otherPartySession.send(str)
//    }
//}