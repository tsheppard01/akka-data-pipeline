package tsheppard01.actors_pubsub

import akka.actor.{Actor, Props}
import org.apache.avro.Schema
import tsheppard01.constants.MessageStage
import tsheppard01.eventbus.{MessageBus, PipelineMessageBus}
import tsheppard01.io.DataSource
import tsheppard01.actors_pubsub.PubDataSourceActor.NextDataRecord

class PubDataSourceActor(messageBus: MessageBus, dataSource: DataSource, schema: Schema)
  extends Actor {

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

  def apply(messageBus: MessageBus, dataSource: DataSource, schema: Schema): Props =
    Props(new PubDataSourceActor(messageBus, dataSource, schema))

  final case class NextDataRecord(record: String, schema: Schema)

  final case object RecordRequest
}
