package tsheppard01.actors

import akka.actor.{Actor, ActorRef, Props}
import tsheppard01.transformation.FieldMasker
import org.apache.avro.generic.GenericData

/**
  * Actor responsible for masking fields in the data
  *
  * @param sinkActorRef The sinkActor to pass masked records to
  * @param fieldMasker The class to use to mask the fields
  */
class FieldMaskingActor(sinkActorRef: ActorRef, fieldMasker: FieldMasker)
    extends Actor {
  override def receive = {
    case FieldMaskingActor.MaskFieldsMessage(record) =>
      sinkActorRef ! DataSinkActor.DataSinkMessage(record)
  }
}

/**
  * Companion object for FieldMaskingActor
  */
object FieldMaskingActor {

  /**
    * Creates Props for FieldMaskingActor
    * This is safest way to create actor props as it avoids closure over the Actors constructor
    * which can be caused by pass-by-name Props(creator => T) method. Parameters to function
    * creator could change between Props function call time and time when creator is
    * executed by the framework
    *
    * @param sinkActorRef The sinkActor to pass masked records to
    * @param fieldMasker The class to use to mask the fields
    */
  def apply(sinkActorRef: ActorRef, fieldMasker: FieldMasker): Props =
    Props(new FieldMaskingActor(sinkActorRef, fieldMasker))

  /**
    * Message containing the record which should have its' fields masked
    * @param record Avro record to mask fields of
    */
  final case class MaskFieldsMessage(record: GenericData.Record)
}
