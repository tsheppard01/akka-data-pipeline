package tsheppard01.actors

import tsheppard01.actors.DataSinkActor.DataSinkMessage
import akka.actor.{Actor, Props}
import tsheppard01.io.DataSink
import org.apache.avro.generic.GenericData

/**
  * Actor responsible for pushing records to a sink
  *
  * @param dataSink Class to handle output of data to a sink
  */
class DataSinkActor(dataSink: DataSink) extends Actor {

  /**
    * Handle messages
    */
  override def receive = {
    case DataSinkMessage(record) =>
      dataSink.writeRecord(record)
  }
}

/**
  * Companion object for DataSinkActor
  */
object DataSinkActor{

  /**
    * Creates Props for DataSinkActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param dataSink The data sink used to output records
    */
  def apply(dataSink: DataSink): Props  = Props(new DataSinkActor(dataSink))

  /**
    * Message containing record to output
    * @param record
    */
  final case class DataSinkMessage(record: GenericData.Record)
}