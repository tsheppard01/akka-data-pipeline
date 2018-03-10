package actors

import actors.ConvertToAvroActor.ConvertToAvroMessage
import akka.actor.{Actor, ActorRef, Props}
import masking.AvroConverter
import org.apache.avro.Schema

class ConvertToAvroActor(fieldMaskingActor: ActorRef,
                         avroConverter: AvroConverter)
    extends Actor {
  override def receive = {
    case ConvertToAvroMessage(record, schema) =>
      val convertedRecord = avroConverter.convertRecordToAvro(record, schema)
      fieldMaskingActor ! FieldMaskingActor.MaskFieldsMessage(convertedRecord)
  }
}

object ConvertToAvroActor {

  def apply(fieldMaskingActor: ActorRef, avroConverter: AvroConverter): Props =
    Props(new ConvertToAvroActor(fieldMaskingActor, avroConverter))

  final case class ConvertToAvroMessage(record: String, schema: Schema)
}
