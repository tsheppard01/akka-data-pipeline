package pubsub.actors

import akka.actor.{Actor, Props}
import masking.AvroConverter
import org.apache.avro.generic.GenericData
import pubsub.actors.PubConvertToAvroActor.ConvertedAvro
import pubsub.eventbus.{MessageBus, MyMessageBus}

class PubConvertToAvroActor(messageBus: MyMessageBus,
                            avroConverter: AvroConverter)
  extends Actor {
  override def receive = {
    case PubDataSourceActor.NextDataRecord(record, schema) =>
      val convertedRecord = avroConverter.convertRecordToAvro(record, schema)
      val event = MessageBus.MessageEvent(
        "ConvertToAvro",
        ConvertedAvro(convertedRecord)
      )
      messageBus.publish(event)
  }
}

object PubConvertToAvroActor {

  def apply(messageBus: MyMessageBus, avroConverter: AvroConverter): Props =
    Props(new PubConvertToAvroActor(messageBus, avroConverter))

  final case class ConvertedAvro(record: GenericData.Record)
}
