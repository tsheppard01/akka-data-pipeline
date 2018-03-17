package tsheppard01.constants

object MessageStage {

  sealed trait MessageStage{
    def source: String
  }

  final case object DataSource extends MessageStage {
    val source = "DataSourceActor"
  }

  final case object ConvertToAvro extends MessageStage {
    val source = "ConverToAvroActor"
  }

  final case object FieldMasking extends MessageStage {
    val source = "FieldMasking Actor"
  }
}
