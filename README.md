<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# Spilt Cordapp Example

An example app showing how a Corda application can be split across multiple workflow CorDapps.

For context, it is a precursor to an application to model goods being sent from a Producer to a Receiver with an IOT Device attached to track location and condition of the goods. Eg these bananas didn't go above 5 degrees for the whole trip. Although this is not implemented in this version.

This application enables the Producer to ask both the Receiver and IOT Device 'who are you?'. Each of them responds with a different message ('I am the receiver' or I am the IOT Device') using a 
bespoke implementation of an abstract responder class, each packaged in their own cordapp.  

## Running the CorDapp

You can use deployNodes to deploy the Application locally.


 Then to start the initiating flow you can: 
 - Use the crash shell:
 
```
flow start WhoAreYouProducerInitiatorFlow parties: [Receiver, IOTDevice]
```
 
 - Use the Corda-Kotlin-Shell. There is a basic script included in Clients/CKSScripts. You will need to download a copy of CKS from the Corda repo (private to R3)
 
 - Modify the Client to trigger the flows (not done in this repo)

Note, only the Producer node can trigger the 'who am I' ... which is kind of the whole point..

## Application Structure

The application has the following modules which each generate a separate CorDapp: 

### contracts 

 - Holds the Ledger level State and Contract classes. 

### commonWorkflows

 - Provides abstract classes which will be implemented in the actor specific workflow cordapps 
 - The abstract classes have placeholder functions which are inherited by the implemented classes.
 - They also allow the various implementations of the flows to link up properly (see 'Linking up the flows' below)

### producerWorkflows

- Provides an implementation of the abstract initiating flow for the producer

### receiverWorkflows

- Provides a bespoke implementation of the abstract responder flow for the Receiver

### iotWorkflows

- Provides a bespoke implementation of the abstract responder flow for the IOT device

### testing

When trying to test the testing has been taken out into its own module. 

This is to keep the structure clean and avoid possible circular dependencies 


## Linking up the flows

We want each of the actors to be able to have their own bespoke version of the flow which resides in their own CorDapp.  We need to make sure any implementation of the abstract initiating flow can be responded to by any bespoke implementation of the abstract Responder flow. To do this:

The abstract WhoAreYouInitiatorFlow is annotated with @InitiatingFLow.

```kotlin
@InitiatingFlow
abstract class WhoAreYouInitiatorFlow(open val parties: List<Party>): FlowLogic<String>(){

    fun someFunction(): String{ return "This is a helper function"}
}
```

The implementations of the abstract responder flows are each annotated with the abstract InitiatingFlow.  

```kotlin
@InitiatedBy(WhoAreYouInitiatorFlow::class)
class WhoAreYouIOTResponderFlow(otherPartySession: FlowSession): WhoAreYouResponderFlow(otherPartySession){}
```
```kotlin
@InitiatedBy(WhoAreYouInitiatorFlow::class)
class WhoAreYouReceiverResponderFlow(otherPartySession: FlowSession): WhoAreYouResponderFlow(otherPartySession){}
```

The flows which implement the abstract InitiatingFLow don't need the @InitiatingFlow annotation 

```kotlin
@StartableByRPC
class WhoAreYouProducerInitiatorFlow(parties: List<Party>): WhoAreYouInitiatorFlow(parties){}
```

Note this is a slightly different way of modifying the Initiator and Responder Flows than described in the docs (https://docs.corda.net/head/flow-overriding.html#configuring-responder-flows) 
In the Docs, super/sub classes are used which allows both the super class and subclasses to be invoked. In this repo the we inherit from abstract classes so they cannot be instantiated themselves meaning that an actor has to use the specific subclass in the specific CorDapp distributed to them. This potentially gives more control over who can do what.

## How the extra modules were added

This Application started off from the cordapp-template-kotlin, which you can find here: https://github.com/corda/cordapp-template-kotlin

To the process to add extra workflow directories was as follows: 
 
 1. Refactor the existing workflows directory name to commonWorkflows
 
 2. Refactor the workflows module to commonWorkflows
 
 3. Change the module name in settings.gradle
 ```groovy
 include 'contracts'
 include 'clients'
 include 'commonWorkflows'
 ```
 
(Probably check it still builds at this point)
 
 4. Copy the commonWorkflow directory and rename it, eg to producerWorkflows (this won't show up as a module yet)
 
 5. Add producerWorkflows into the gradle.settings file. This will tell gradle it's a module 
 
```groovy
include 'contracts'
include 'clients'
include 'commonWorkflows'
include 'producerWorkflows'
```

 6. In the producerWorkflows build.gradle add the dependency on commonWorkflows, you can delete the dependency on contracts as this will be pulled in by commonWorkflows

```groovy
dependencies {
     ...
    // CorDapp dependencies.
    cordapp project(":commonWorkflows")
}
```
(Probably check it still builds at this point)

 7. Repeat same process to create the receiverWorkflows and iotworkflows

The dependency structure ended up as: 

 - commonWorkFlows -> contracts
 - producerWokflows -> commonWorkflows
 - receiverWorkflows -> commonWorkflows
 - iotWorkFlows ->  commonWorkflows


## Modifying deployNodes

The update to deploynodes was as follows:

 1. Update the nodeDefault, change workflows to commonWorkflows, this makes sure all the nodes have these CorDapps: 
 
 ```groovy
    nodeDefaults {
        projectCordapp {
            deploy = false
        }
        cordapp project(':contracts')
        cordapp project(':commonWorkflows')

    } 
```  

 2. Add one extra node changing the port addresses by adding 3 to each
 
 3. Update each individual node to be called Producer, Receiver IOTDevice (leave the notary as is)
 
 3. Add the relevant cordapps to the node, eg: 
 
 ```groovy
 node {
         name "O=Producer,L=London,C=GB"
         p2pPort 10005
         rpcSettings {
             address("localhost:10006")
             adminAddress("localhost:10046")
         }
         rpcUsers = [[ user: "user1", "password": "test", "permissions": ["ALL"]]]
         cordapp project(':producerWorkflows')
     }
```










