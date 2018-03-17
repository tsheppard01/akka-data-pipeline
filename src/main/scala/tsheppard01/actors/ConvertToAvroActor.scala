package tsheppard01.actors

import tsheppard01.actors.ConvertToAvroActor.ConvertToAvroMessage
import akka.actor.{Actor, ActorRef, Props}
import tsheppard01.transformation.StringToAvroRecordConverter
import org.apache.avro.Schema

/**
  * Actor to convert a string representation of record to avro
  *
  * @param fieldMaskingActor The actor to send converted avro to
  * @param avroConverter Class  to convert string records to avro
  */
class ConvertToAvroActor(fieldMaskingActor: ActorRef,
                         avroConverter: StringToAvroRecordConverter)
    extends Actor {
  override def receive = {
    case ConvertToAvroMessage(record, schema) =>
      val convertedRecord = avroConverter.convert(record, schema)
      fieldMaskingActor ! FieldMaskingActor.MaskFieldsMessage(convertedRecord)
  }
}

/**
  * Companion object for the ConvertToAvroActor
  */
object ConvertToAvroActor {

  /**
    * Creates Props for ConvertToAvroActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param fieldMaskingActor The actor to send converted avro to
    * @param avroConverter Class  to convert string records to avro
    */
  def apply(fieldMaskingActor: ActorRef, avroConverter: StringToAvroRecordConverter): Props =
    Props(new ConvertToAvroActor(fieldMaskingActor, avroConverter))

  /**
    * Message containing the record to convert to avro and the schema to use in conversion

    * @param record The string record to convert
    * @param schema The schema to use in conversion
    */
  final case class ConvertToAvroMessage(record: String, schema: Schema)
}
