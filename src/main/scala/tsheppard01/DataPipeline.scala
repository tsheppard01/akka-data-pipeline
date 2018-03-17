package tsheppard01

import akka.actor.{ActorRef, ActorSystem}
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

    val actorSystem = ActorSystem("Simple_data_pipeline")

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
        DataSinkActor(dataSink),
        name = "DataSinkActor"
      )

    val fieldMaskingActor: ActorRef =
      actorSystem.actorOf(
        FieldMaskingActor(dataSinkActor, fieldMasker),
        name = "FieldMaskingActor"
      )

    val convertToAvroActor: ActorRef =
      actorSystem.actorOf(
        ConvertToAvroActor(fieldMaskingActor, avroConverter),
          name = "ConvertDelimitedToAvroActor"
      )

    val dataSourceActor: ActorRef =
      actorSystem.actorOf(
        DataSourceActor(convertToAvroActor, dataSource, schema),
        name = "SourceForSourceA"
      )

    List.range(1,100)
      .foreach{ _ =>
        dataSourceActor ! DataSourceActor.NextMessage
      }

    Await.result(actorSystem.whenTerminated, Duration.Inf)
    sys.exit(0)
  }

}
