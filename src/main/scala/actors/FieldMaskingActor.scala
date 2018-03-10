package actors

import akka.actor.{Actor, ActorRef, Props}
import masking.FieldMasker
import org.apache.avro.generic.GenericData

class FieldMaskingActor(sinkActorRef: ActorRef, fieldMasker: FieldMasker)
    extends Actor {
  override def receive = {
    case FieldMaskingActor.MaskFieldsMessage(record) =>
      sinkActorRef ! DataSinkActor.DataSinkMessage(record)
  }
}

object FieldMaskingActor {

  def apply(sinkActorRef: ActorRef, fieldMasker: FieldMasker): Props =
    Props(new FieldMaskingActor(sinkActorRef, fieldMasker))

  final case class MaskFieldsMessage(record: GenericData.Record)
}
