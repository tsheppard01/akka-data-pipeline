package tsheppard01

import akka.actor.{ActorRef, ActorSystem}
import akka.routing.FromConfig
import org.apache.avro.Schema
import tsheppard01.actors.{ConvertToAvroActor, DataSinkActor, DataSourceActor, FieldMaskingActor}
import tsheppard01.io.{CsvGeneratorDataSource, LogDataSink}
import tsheppard01.transformation.{CsvStringToAvroConverter, MarkedFieldMasker}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Example of a simple data pipeline use Akka Actor system.
  *
  * System Path:
  *
  *   DataSourceActor -> ConvertToAvroActor -> FieldMaskingActor -> DataSink
  *
  * DataSourceActor generates a csv record,
  * ConvertToAvroActor converts this csv record to avro,
  * FieldMaskingActor masks fields specified in the avro schema,
  * DataSink outputs to the data to logs
  *
  * Purpose is to show how to set up a basic data pipeline using akka actors.
  * Path of the pipeline is defined inside actors, each actor contains a ref to
  * the next actor in the chain. Work is injected into the actor system via
  * the main method which sends NextRecord messages to the DataSourceActor.
  *
  */
object DataPipeline {

  def main(args: Array[String]): Unit ={

    val actorSystem = ActorSystem("DataPipeline")

    val dataSource = new CsvGeneratorDataSource()
    val dataSink = new LogDataSink()
    val avroConverter = new CsvStringToAvroConverter()
    val fieldMasker = new MarkedFieldMasker()

    val schemaStream = getClass.getResourceAsStream("/Test.avsc")
    val schema = new Schema.Parser().parse(schemaStream)

    /**
      * Actor routers, dispathcers and number of instances
      * are defined in application.conf
      */
    val dataSinkActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(DataSinkActor(dataSink)),
        name = "DataSinkActor"
      )

    val fieldMaskingActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(FieldMaskingActor(dataSinkActor, fieldMasker)),
        name = "FieldMaskingActor"
      )

    val convertToAvroActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(ConvertToAvroActor(fieldMaskingActor, avroConverter)),
        name = "ConvertToAvroActor"
      )

    val dataSourceActor: ActorRef =
      actorSystem.actorOf(
        FromConfig.props(DataSourceActor(convertToAvroActor, dataSource, schema)),
        name = "DataSourceActor"
      )

    List.range(1,10000)
      .foreach{ _ =>
        dataSourceActor ! DataSourceActor.NextMessage
      }

    Await.result(actorSystem.whenTerminated, Duration.Inf)
    sys.exit(0)
  }

}
