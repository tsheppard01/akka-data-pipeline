package tsheppard01.actors_pubsub

import akka.actor.{Actor, ActorLogging, Props}
import org.apache.avro.generic.GenericData
import tsheppard01.actors_pubsub.PubConvertToAvroActor.ConvertedAvro
import tsheppard01.constants.MessageStage
import tsheppard01.eventbus.{MessageBus, PipelineMessageBus}
import tsheppard01.transformation.StringToAvroRecordConverter

import scala.util.{Failure, Success, Try}

class PubConvertToAvroActor(messageBus: MessageBus,
                            avroConverter: StringToAvroRecordConverter)
  extends Actor with ActorLogging {
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

object PubConvertToAvroActor {

  def apply(messageBus: MessageBus, avroConverter: StringToAvroRecordConverter): Props =
    Props(new PubConvertToAvroActor(messageBus, avroConverter))

  final case class ConvertedAvro(record: GenericData.Record)
}
