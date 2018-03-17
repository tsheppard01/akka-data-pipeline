package tsheppard01.actors_pubsub

import akka.actor.{Actor, ActorLogging, Props}
import tsheppard01.io.DataSink

/**
  * Actor responsible for pushing records to a sink
  *
  * @param dataSink Class to handle output of data to a sink
  */
class PubDataSinkActor(dataSink: DataSink) extends Actor with ActorLogging {

  /**
    * Handle messages
    */
  override def receive = {
    case PubConvertToAvroActor.ConvertedAvro(record) =>
      dataSink.writeRecord(record)

    /**
      * Extend to consume FieldsMaskedMessages
      */
    case PubFieldMaskingActor.FieldsMaskedMessage(record) =>
      dataSink.writeRecord(record)
  }
}

/**
  * Companion object for PubDataSinkActor
  */
object PubDataSinkActor{

  /**
    * Creates Props for PubDataSinkActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param dataSink The data sink used to output records
    */
  def apply(dataSink: DataSink): Props  = Props(new PubDataSinkActor(dataSink))
}
