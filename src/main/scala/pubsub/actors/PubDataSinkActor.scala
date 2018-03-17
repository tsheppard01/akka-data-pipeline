package pubsub.actors

import akka.actor.{Actor, Props}
import tsheppard01.io.DataSink

class PubDataSinkActor(dataSink: DataSink) extends Actor {
  override def receive = {
    case PubConvertToAvroActor.ConvertedAvro(record) =>
      dataSink.writeRecord(record)
    case PubFieldMaskingActor.FieldsMaskedMessage(record) =>
      dataSink.writeRecord(record)
  }
}

object PubDataSinkActor{
  def apply(dataSink: DataSink): Props  = Props(new PubDataSinkActor(dataSink))
}
