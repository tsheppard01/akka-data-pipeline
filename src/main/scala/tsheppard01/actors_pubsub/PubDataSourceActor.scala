package tsheppard01.actors_pubsub

import akka.actor.{Actor, Props}
import org.apache.avro.Schema
import tsheppard01.constants.MessageStage
import tsheppard01.eventbus.{MessageBus, PipelineMessageBus}
import tsheppard01.io.DataSource
import tsheppard01.actors_pubsub.PubDataSourceActor.NextDataRecord

/**
  * Actor responsible for getting data into the actor system
  *
  * @param messageBus The message event bus to publis to
  * @param dataSource A data source, supplies records for the system
  * @param schema Schema of the records produced by dataSource
  */
class PubDataSourceActor(messageBus: MessageBus, dataSource: DataSource, schema: Schema)
  extends Actor {

  /**
    * Handle messages
    */
  override def receive = {
    case _ =>
      val event: PipelineMessageBus.MessageEvent = PipelineMessageBus.MessageEvent(
        MessageStage.DataSource.source,
        NextDataRecord(dataSource.nextRecord(), schema)
      )
      messageBus.publish(event)
  }
}

object PubDataSourceActor {

  /**
    * Creates Props for PubDataSourceActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param messageBus The message event bus to publis to
    * @param dataSource A data source, supplies records for the system
    * @param schema Schema of the records produced by dataSource
    */
  def apply(messageBus: MessageBus, dataSource: DataSource, schema: Schema): Props =
    Props(new PubDataSourceActor(messageBus, dataSource, schema))

  /**
    * Message containing the next record to be processed
    *
    * @param record String representation of record
    * @param schema Schema of the record
    */
  final case class NextDataRecord(record: String, schema: Schema)
}
