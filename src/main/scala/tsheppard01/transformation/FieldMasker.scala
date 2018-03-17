package tsheppard01.transformation

import org.apache.avro.generic.GenericData

/**
  * Trait for masking fields in an avro record
  */
trait FieldMasker {

  /**
    * Method to mask fields in a record
    * @param record
    * @return
    */
  def maskFieldsInRecord(record: GenericData.Record): GenericData.Record
}

/**
  * Class for masking marked fields in an avro records
  */
class MarkedFieldMasker extends FieldMasker {

  /**
    * Method to replace marked fields in avro record with the string "nonsense".
    * Fields are identified in an avro schema property name "maskedFields", a comma
    * spearated string
    *
    * @param record The record to mask the fields of
    * @return The masked record
    */
  override def maskFieldsInRecord(record: GenericData.Record): GenericData.Record = {
    record.getSchema.getProp("maskedFields").split("\\,")
      .foreach{ field =>
        record.put(field, "nonsense")
      }
    record
  }
}
