package masking

import org.apache.avro.generic.GenericData

trait FieldMasker {

  def maskFieldsInRecord(record: GenericData.Record): GenericData.Record
}

class MarkedFieldMasker() extends FieldMasker {
  override def maskFieldsInRecord(record: GenericData.Record): GenericData.Record = {
    record.getSchema.getProp("maskedFields").split("\\,")
      .foreach{ field =>
        record.put(field, "nonsense")
      }
    record
  }
}
