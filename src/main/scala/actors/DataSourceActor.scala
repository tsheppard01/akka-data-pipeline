package actors

import akka.actor.{Actor, ActorRef, Props}
import io.DataSource
import org.apache.avro.Schema

class DataSourceActor(convertToAvroActor: ActorRef, dataSource: DataSource, schema: Schema)
    extends Actor {
  override def receive = {
    case _ =>
      convertToAvroActor ! ConvertToAvroActor.ConvertToAvroMessage(dataSource.nextRecord(), schema)
  }
}

object DataSourceActor {

  def apply(convertToAvroActor: ActorRef, dataSource: DataSource, schema: Schema): Props =
    Props(new DataSourceActor(convertToAvroActor, dataSource, schema))

  final case object NextMessage
}
