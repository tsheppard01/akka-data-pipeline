package tsheppard01.actors_pubsub

import akka.actor.{Actor, Props}
import org.apache.avro.generic.GenericData
import tsheppard01.constants.MessageStage
import tsheppard01.eventbus.{MessageBus, PipelineMessageBus}
import tsheppard01.actors_pubsub.PubFieldMaskingActor.FieldsMaskedMessage
import tsheppard01.transformation.FieldMasker

/**
  * Actor responsible for masking fields in the data
  *
  * @param messageBus The event bus to publish messages to
  * @param fieldMasker The class to use to mask the fields
  */
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

/**
  * Companion Object to PubFileMaskingActor
  */
object PubFieldMaskingActor {

  /**
    * Creates Props for PubFieldMaskingActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param messageBus The event bus to publish messages to
    * @param fieldMasker The class to use to mask the fields
    */
  def apply(messageBus: MessageBus, fieldMasker: FieldMasker): Props =
    Props(new PubFieldMaskingActor(messageBus, fieldMasker))

  /**
    * Message containing the avro record with masked fields
    *
    * @param record The avro record with masked fields
    */
  final case class FieldsMaskedMessage(record: GenericData.Record)
}
