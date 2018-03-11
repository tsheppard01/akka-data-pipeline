package pubsub.actors

import akka.actor.{Actor, Props}
import masking.FieldMasker
import org.apache.avro.generic.GenericData
import pubsub.actors.PubFieldMaskingActor.FieldsMaskedMessage
import pubsub.eventbus.{MessageBus, MyMessageBus}

class PubFieldMaskingActor(messageBus: MyMessageBus, fieldMasker: FieldMasker)
  extends Actor {
  override def receive = {
    case PubConvertToAvroActor.ConvertedAvro(record) =>
      val maskedRecord = fieldMasker.maskFieldsInRecord(record)
      val event = MessageBus.MessageEvent(
        "FieldMasking",
        FieldsMaskedMessage(maskedRecord)
      )

      messageBus.publish(event)
  }
}

object PubFieldMaskingActor {

  def apply(messageBus: MyMessageBus, fieldMasker: FieldMasker): Props =
    Props(new PubFieldMaskingActor(messageBus, fieldMasker))

  final case class FieldsMaskedMessage(record: GenericData.Record)
}
