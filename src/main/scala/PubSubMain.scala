import akka.actor.ActorSystem
import tsheppard01.io.{CsvGeneratorDataSource, LogDataSink}
import tsheppard01.transformation.{DelimitedAvroConverter, MarkedFieldMasker}
import org.apache.avro.Schema
import pubsub.actors.{PubConvertToAvroActor, PubDataSinkActor, PubDataSourceActor, PubFieldMaskingActor}
import pubsub.eventbus.MessageBus

object PubSubMain {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("PubSubDataPipeline")

    val dataSource = new CsvGeneratorDataSource()
    val dataSink = new LogDataSink()
    val avroConveter = new DelimitedAvroConverter(",")
    val fieldMasker = new MarkedFieldMasker()

    val schemaStream = getClass.getResourceAsStream("/Test.avsc")
    val schema = new Schema.Parser().parse(schemaStream)

    val messageBus = new MessageBus()

    val dataSourceActor = actorSystem.actorOf(
      PubDataSourceActor(messageBus, dataSource, schema),
      name = "dataSource"
    )

    val convertToAvroActor = actorSystem.actorOf(
      PubConvertToAvroActor(messageBus, avroConveter),
      name = "converter"
    )

    val fieldMaskerActor = actorSystem.actorOf(
      PubFieldMaskingActor(messageBus, fieldMasker)
    )

    val dataSinkActor = actorSystem.actorOf(
      PubDataSinkActor(dataSink),
      name = "dataSink"
    )

    messageBus.subscribe(dataSinkActor, "FieldMasking")
    messageBus.subscribe(dataSinkActor, "ConvertToAvro")
    messageBus.subscribe(fieldMaskerActor, "ConvertToAvro")
    messageBus.subscribe(convertToAvroActor, "DataSourceActor")
    messageBus.subscribe(dataSourceActor, "GetData")

    List.range(1, 100)
      .foreach { _ =>
        messageBus.publish(MessageBus.MessageEvent("GetData", PubDataSourceActor.RecordRequest))
      }
  }
}
