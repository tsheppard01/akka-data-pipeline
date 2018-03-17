package tsheppard01.actors_pubsub

import akka.actor.{Actor, ActorLogging, Props}
import org.apache.avro.generic.GenericData
import tsheppard01.actors_pubsub.PubConvertToAvroActor.ConvertedAvro
import tsheppard01.constants.MessageStage
import tsheppard01.eventbus.{MessageBus, PipelineMessageBus}
import tsheppard01.transformation.StringToAvroRecordConverter
import scala.util.{Failure, Success, Try}

/**
  * Actor to convert a string representation of record to avro
  *
  * @param messageBus The event bus to publish messages to
  * @param avroConverter Class to convert string records to avro
  */
class PubConvertToAvroActor(messageBus: MessageBus,
                            avroConverter: StringToAvroRecordConverter)
  extends Actor with ActorLogging {

  /**
    * Handle messages
    */
  override def receive = {
    case PubDataSourceActor.NextDataRecord(record, schema) =>
      val convertTry = Try(avroConverter.convert(record, schema))
      convertTry match {
        case Success(convertedRecord) =>
          val event = PipelineMessageBus.MessageEvent(
            MessageStage.ConvertToAvro.source,
            ConvertedAvro(convertedRecord)
          )
          messageBus.publish(event)
        case Failure(e) =>
          log.error(s"Failed to convert record. ${e.getMessage} \n $record")
      }
  }
}

/**
  * Companion object
  */
object PubConvertToAvroActor {

  /**
    * Creates Props for PubConvertToAvroActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param messageBus The event bus to publish messages to
    * @param avroConverter Class  to convert string records to avro
    */
  def apply(messageBus: MessageBus, avroConverter: StringToAvroRecordConverter): Props =
    Props(new PubConvertToAvroActor(messageBus, avroConverter))

  /**
    * Message contaiing the converted avro record
    *
    * @param record The converted avro record
    */
  final case class ConvertedAvro(record: GenericData.Record)
}
