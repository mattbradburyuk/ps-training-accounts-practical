package com.template.producer.flows

import co.paralleluniverse.fibers.Suspendable

import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********

//@InitiatingFlow
//@StartableByRPC
//class WhoAreYouProducerInitiatorFlow(parties: List<Party>): WhoAreYouInitiatorFlow(parties){

//@InitiatingFlow
//@StartableByRPC
//class WhoAreYouProducerInitiatorFlow(val parties: List<Party>): FlowLogic<String>(){
//
//    @Suspendable
//    override fun call(): String {
//
//        val sessions = parties.map { initiateFlow(it) }
//        val unsafeResults = sessions.map {it.sendAndReceive<String>("Who are you?")}
//        val results = unsafeResults.map { it.unwrap {}  }
//        var str = "Messages:\n"
//        for (r in results) str = "$str $r "
//        return str
//    }
//}