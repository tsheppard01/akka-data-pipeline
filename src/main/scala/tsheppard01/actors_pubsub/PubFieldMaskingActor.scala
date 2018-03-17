package tsheppard01.actors_pubsub

import akka.actor.{Actor, Props}
import org.apache.avro.generic.GenericData
import tsheppard01.constants.MessageStage
import tsheppard01.eventbus.{MessageBus, PipelineMessageBus}
import tsheppard01.actors_pubsub.PubFieldMaskingActor.FieldsMaskedMessage
import tsheppard01.transformation.FieldMasker

class PubFieldMaskingActor(messageBus: MessageBus, fieldMasker: FieldMasker)
  extends Actor {

  /**
    * Handle messages
    */
  override def receive = {
    case PubConvertToAvroActor.ConvertedAvro(record) =>
      val maskedRecord = fieldMasker.maskFieldsInRecord(record)
      val event = PipelineMessageBus.MessageEvent(
        MessageStage.FieldMasking.source,
        FieldsMaskedMessage(maskedRecord)
      )

      messageBus.publish(event)
  }
}

object PubFieldMaskingActor {

  def apply(messageBus: MessageBus, fieldMasker: FieldMasker): Props =
    Props(new PubFieldMaskingActor(messageBus, fieldMasker))

  final case class FieldsMaskedMessage(record: GenericData.Record)
}
