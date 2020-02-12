package com.template.common.flows

import net.corda.core.flows.*
import net.corda.core.identity.Party

// *********
// * Flows *
// *********

// Note, abstractclass shouldn't be startable by RPC
@InitiatingFlow
abstract class WhoAreYouInitiatorFlow(open val parties: List<Party>): FlowLogic<String>(){

    fun someFunction(): String{ return "This is a helper function"}
}

// note subclass needs @InitiatedBy not the abstract class
abstract class WhoAreYouResponderFlow(val otherPartySession: FlowSession): FlowLogic<Unit>(){

    fun someOtherFunction(): String{ return "This is another helper function"}
}
