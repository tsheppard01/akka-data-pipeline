package tsheppard01.actors

import akka.actor.{Actor, ActorRef, Props}
import org.apache.avro.Schema
import tsheppard01.actors.DataSourceActor.ReadRecordMessage
import tsheppard01.io.DataSource

/**
  * Actor responsible for getting data into the actor system
  *
  * @param convertToAvroActor The actor to send data to
  * @param dataSource A data source, supplies records for the system
  * @param schema Schema of the records produced by dataSource
  */
class DataSourceActor(convertToAvroActor: ActorRef, dataSource: DataSource, schema: Schema)
    extends Actor {

  /**
    * Handle messages
    */
  override def receive = {
    case ReadRecordMessage =>
      convertToAvroActor ! ConvertToAvroActor.ConvertToAvroMessage(dataSource.nextRecord(), schema)
  }
}

/**
  * Companion object for the DataSourceActor
  */
object DataSourceActor {

  /**
    * Creates Props for DataSourceActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param convertToAvroActor The data sink used to output records
    * @param dataSource A data source, supplies records for the system
    * @param schema Schema of the records produced by dataSource
    */
  def apply(convertToAvroActor: ActorRef, dataSource: DataSource, schema: Schema): Props =
    Props(new DataSourceActor(convertToAvroActor, dataSource, schema))

  /**
    * Message to indicate next record should be retrieved and pushed into system
    */
  final case object ReadRecordMessage
}
