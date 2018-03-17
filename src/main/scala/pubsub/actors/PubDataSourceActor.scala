package pubsub.actors

import akka.actor.{Actor, Props}
import tsheppard01.io.DataSource
import org.apache.avro.Schema
import pubsub.actors.PubDataSourceActor.NextDataRecord
import pubsub.eventbus.{MessageBus, MyMessageBus}

class PubDataSourceActor(messageBus: MyMessageBus, dataSource: DataSource, schema: Schema)
  extends Actor {

  override def receive = {
    case _ =>
      val event: MessageBus.MessageEvent = MessageBus.MessageEvent(
        "DataSourceActor",
        NextDataRecord(dataSource.nextRecord(), schema)
      )
      messageBus.publish(event)
  }
}

object PubDataSourceActor {

  def apply(messageBus: MyMessageBus, dataSource: DataSource, schema: Schema): Props =
    Props(new PubDataSourceActor(messageBus, dataSource, schema))

  final case class NextDataRecord(record: String, schema: Schema)

  final case object RecordRequest
}
