<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# Spilt Cordapp Example

An example app showing how a application can be split across multiple workflow cordapps.

For Context, it is a precursor to a App to model goods being sent from a Producer to a Receiver with an IOT Device attached to track location and condition of the goods. Eg these bananas didn't go above 5 degrees for the whole trip.

The App enables the Producer to ask both the Receiver and IOT Device 'who are you?'. Each of them responds with a different message ('I am the receiver' or I am the IOT Device') using a 
bespoke implementation of an abstract responder class, each packaged in their own cordapp.  

The App has the following Cordapps/ modules

### contracts 

 - Holds the Ledger level State and Contract classes. 

### commonWorkflows

 - Provides abstract classes which will be implemented in the actor specific workflow cordapps 
 - The abstract classes have placeholder functions which are inherited by the sub classes.
 - They also allow subclassing so that the subclassed flows still link up (see 'Linking up the flows' below)

### producerWorkflows

- Provides an implementation of the abstract initiating flow for the producer

### receiverWorkflows

- Provides a bespoke implementation of the abstract responder flow for the Receiver

### iotWorkflows

- Provides a bespoke implementation of the abstract responder flow for the IOT device

### testing

When trying to test the testing has to be taken out into its own module. 

If you try and write your tests within one of the cordapp modules then you end up with Circular dependencies. 

*** Think about this ***


## Linking up the flows

We want each of the actors to be able to have their own bespoke version of the flow.  We need to make sure any subclassed flow of the abstract initiating flow can be responded to by any subclassed flow of the abstracted Responder flow.  

The abstract WhoAreYouInitiatorFlow is annotated with @InitiatingFLow.

```kotlin
@InitiatingFlow
abstract class WhoAreYouInitiatorFlow(open val parties: List<Party>): FlowLogic<String>(){

    fun someFunction(): String{ return "This is a helper function"}
}
```

The subclassed responder flows are each be annotated with the abstract InitiatingFlow.  

```kotlin
@InitiatedBy(WhoAreYouInitiatorFlow::class)
class WhoAreYouIOTResponderFlow(otherPartySession: FlowSession): WhoAreYouResponderFlow(otherPartySession){}
}
```
```kotlin
@InitiatedBy(WhoAreYouInitiatorFlow::class)
class WhoAreYouReceiverResponderFlow(otherPartySession: FlowSession): WhoAreYouResponderFlow(otherPartySession){}
```

The flows which subclass the abstract InitiatingFLow don't need the @InitiatingFlow annotation 

```kotlin
@StartableByRPC
class WhoAreYouProducerInitiatorFlow(parties: List<Party>): WhoAreYouInitiatorFlow(parties){}
```

Note this is a slightly different way of modifying the Initiator and responder Flows than described in the docs (https://docs.corda.net/head/flow-overriding.html#configuring-responder-flows) 
In the Docs both the super class and subclasses can be called, in this example the super classes are abstract so they cannot be instantiated themselves meaning that an actor has to use the specific subclass in the cordaap allocated to them. ie it removes the choice of using an standard flow distributed to all actors. 

## Dependency structure

commonWorkFlows -> contracts

producerWokflows -> contracts, commonWorkflows
receiverWorkflows -> copntracts, commonWorkflows
iotWorkFlows -> contracts commonWorkflows



## Modifying deployNodes










Testing challenge

**This is the Kotlin version of the CorDapp template. The Java equivalent is 
[here](https://github.com/corda/cordapp-template-java/).**

# Pre-Requisites

See https://docs.corda.net/getting-set-up.html.

# Usage

## Running tests inside IntelliJ

We recommend editing your IntelliJ preferences so that you use the Gradle runner - this means that the quasar utils
plugin will make sure that some flags (like ``-javaagent`` - see below) are
set for you.

To switch to using the Gradle runner:

* Navigate to ``Build, Execution, Deployment -> Build Tools -> Gradle -> Runner`` (or search for `runner`)
  * Windows: this is in "Settings"
  * MacOS: this is in "Preferences"
* Set "Delegate IDE build/run actions to gradle" to true
* Set "Run test using:" to "Gradle Test Runner"

If you would prefer to use the built in IntelliJ JUnit test runner, you can run ``gradlew installQuasar`` which will
copy your quasar JAR file to the lib directory. You will then need to specify ``-javaagent:lib/quasar.jar``
and set the run directory to the project root directory for each test.

## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Interacting with the nodes

### Shell

When started via the command line, each node will display an interactive shell:

    Welcome to the Corda interactive shell.
    Useful commands include 'help' to see what is available, and 'bye' to shut down the node.
    
    Tue Nov 06 11:58:13 GMT 2018>>>

You can use this shell to interact with your node. For example, enter `run networkMapSnapshot` to see a list of 
the other nodes on the network:

    Tue Nov 06 11:58:13 GMT 2018>>> run networkMapSnapshot
    [
      {
      "addresses" : [ "localhost:10002" ],
      "legalIdentitiesAndCerts" : [ "O=Notary, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505484825
    },
      {
      "addresses" : [ "localhost:10005" ],
      "legalIdentitiesAndCerts" : [ "O=PartyA, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505382560
    },
      {
      "addresses" : [ "localhost:10008" ],
      "legalIdentitiesAndCerts" : [ "O=PartyB, L=New York, C=US" ],
      "platformVersion" : 3,
      "serial" : 1541505384742
    }
    ]
    
    Tue Nov 06 12:30:11 GMT 2018>>> 

You can find out more about the node shell [here](https://docs.corda.net/shell.html).

### Client

`clients/src/main/kotlin/com/template/Client.kt` defines a simple command-line client that connects to a node via RPC 
and prints a list of the other nodes on the network.

#### Running the client

##### Via the command line

Run the `runTemplateClient` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`.

##### Via IntelliJ

Run the `Run Template Client` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`.

### Webserver

`clients/src/main/kotlin/com/template/webserver/` defines a simple Spring webserver that connects to a node via RPC and 
allows you to interact with the node over HTTP.

The API endpoints are defined here:

     clients/src/main/kotlin/com/template/webserver/Controller.kt

And a static webpage is defined here:

     clients/src/main/resources/static/

#### Running the webserver

##### Via the command line

Run the `runTemplateServer` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

##### Via IntelliJ

Run the `Run Template Server` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

#### Interacting with the webserver

The static webpage is served on:

    http://localhost:10050

While the sole template endpoint is served on:

    http://localhost:10050/templateendpoint
    
# Extending the template

You should extend this template as follows:

* Add your own state and contract definitions under `contracts/src/main/kotlin/`
* Add your own flow definitions under `workflows/src/main/kotlin/`
* Extend or replace the client and webserver under `clients/src/main/kotlin/`

For a guided example of how to extend this template, see the Hello, World! tutorial 
[here](https://docs.corda.net/hello-world-introduction.html).
