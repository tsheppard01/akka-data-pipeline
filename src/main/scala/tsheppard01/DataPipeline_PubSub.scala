package tsheppard01

import akka.actor.{ActorRef, ActorSystem}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import org.apache.avro.Schema
import tsheppard01.actors.DataSinkActor
import tsheppard01.actors_pubsub.{PubConvertToAvroActor, PubDataSinkActor, PubDataSourceActor, PubFieldMaskingActor}
import tsheppard01.constants.MessageStage
import tsheppard01.eventbus.PipelineMessageBus
import tsheppard01.io.{CsvGeneratorDataSource, LogDataSink}
import tsheppard01.transformation.{CsvStringToAvroConverter, MarkedFieldMasker}

object DataPipeline_PubSub {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()

    val actorSystem = ActorSystem("DataPipeline_PubSub")

    val dataSource = new CsvGeneratorDataSource()
    val dataSink = new LogDataSink()
    val avroConverter = new CsvStringToAvroConverter()
    val fieldMasker = new MarkedFieldMasker()

    val schemaStream = getClass.getResourceAsStream("/Test.avsc")
    val schema = new Schema.Parser().parse(schemaStream)

    val messageBus = new PipelineMessageBus()

    /**
      * Actor routers, dispathcers and number of instances
      * are defined in application.conf
      */
    val dataSinkActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(PubDataSinkActor(dataSink)),
        name = "DataSinkActor"
      )

    val fieldMaskingActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(PubFieldMaskingActor(messageBus, fieldMasker)),
        name = "FieldMaskingActor"
      )

    val convertToAvroActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(PubConvertToAvroActor(messageBus, avroConverter)),
        name = "ConvertToAvroActor"
      )

    val dataSourceActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(PubDataSourceActor(messageBus, dataSource, schema)),
        name = "DataSourceActor"
      )

    /**
      * Setup all message flow through subscriptions to the message bus
      */
    messageBus.subscribe(dataSinkActor, MessageStage.FieldMasking.source)
    messageBus.subscribe(dataSinkActor, MessageStage.ConvertToAvro.source)
    messageBus.subscribe(fieldMaskingActor, MessageStage.ConvertToAvro.source)
    messageBus.subscribe(convertToAvroActor, MessageStage.DataSource.source)
    messageBus.subscribe(dataSourceActor, "GetData")

    List.range(1,config.getLong("app.generated-data.num-records"))
      .foreach { _ =>
        messageBus.publish(PipelineMessageBus.MessageEvent("GetData", PubDataSourceActor.RecordRequest))
      }
  }
}
