package com.template.producer.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.common.flows.WhoAreYouInitiatorFlow

import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********

// Implemented class shouldn't have @InitiatingFlow
@StartableByRPC
class WhoAreYouProducerInitiatorFlow(parties: List<Party>): WhoAreYouInitiatorFlow(parties){

    @Suspendable
    override fun call(): String {
        logger.info("MB: WhoAreYouProducerInitiatorFlow called")
        logger.info("MB: test someFunction(): ${someFunction()}")
        val sessions = parties.map { initiateFlow(it) }
        val unsafeResults = sessions.map {it.sendAndReceive<String>("Who are you?")}
        val results = unsafeResults.map { usd -> usd.unwrap {it}  }
        var str = "Messages:"
        for (r in results) str = "$str $r "
        return str
    }
}