package actors

import actors.DataSinkActor.DataSinkMessage
import akka.actor.{Actor, Props}
import io.DataSink
import org.apache.avro.generic.GenericData

class DataSinkActor(dataSink: DataSink) extends Actor {
  override def receive = {
    case DataSinkMessage(record) =>
      dataSink.writeRecord(record)
  }
}

object DataSinkActor{

  def apply(dataSink: DataSink): Props  = Props(new DataSinkActor(dataSink))

  final case class DataSinkMessage(record: GenericData.Record)
}