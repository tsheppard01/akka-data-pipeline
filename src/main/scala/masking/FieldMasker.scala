package masking

trait FieldMasker {

  def maskFieldsInRecord(record: String): String
}

class MarkedFieldMasker() extends FieldMasker {
  override def maskFieldsInRecord(record: String) = ???
}
