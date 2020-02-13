import helpers.*
import com.template.common.flows.*
import com.template.receiver.flows.*
import com.template.producer.flows.*

val producer = connect(10006)
val receiver = connect(10009)
val iotdevice = connect(10012)

/*

// works
producer.start(WhoAreYouProducerInitiatorFlow::class.java, listOf(receiver.party()))

// works
producer.start(WhoAreYouProducerInitiatorFlow::class.java, listOf(iotdevice.party()))

// works
producer.start(WhoAreYouProducerInitiatorFlow::class.java, listOf(receiver.party(), iotdevice.party()))
// errors
receiver.start(WhoAreYouProducerInitiatorFlow::class.java, listOf(iotdevice.party()))

// errors
producer.start(WhoAreYouProducerInitiatorFlow::class.java, listOf(producer.party()))
*/