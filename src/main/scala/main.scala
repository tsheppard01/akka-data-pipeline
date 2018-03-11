import actors.{ConvertToAvroActor, DataSinkActor, DataSourceActor, FieldMaskingActor}
import akka.actor.{ActorRef, ActorSystem}
import io.{CsvGeneratorDataSource, DummyDataSink}
import masking.{DelimitedAvroConverter, MarkedFieldMasker}
import org.apache.avro.Schema

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object main {

  def main(args: Array[String]): Unit ={

    val actorSystem = ActorSystem("Simple_data_pipeline")

    val dataSource = new CsvGeneratorDataSource()
    val dataSink = new DummyDataSink()
    val avroConverter = new DelimitedAvroConverter(",")
    val fieldMasker = new MarkedFieldMasker()

    val schemaStream = getClass.getResourceAsStream("/Test.avsc")
    val schema = new Schema.Parser().parse(schemaStream)

    val dataSinkActor: ActorRef =
      actorSystem.actorOf(
        DataSinkActor(dataSink),
        name = "DummyDataSinkActor"
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
