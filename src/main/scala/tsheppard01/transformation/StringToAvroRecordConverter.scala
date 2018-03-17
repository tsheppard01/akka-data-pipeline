package tsheppard01.transformation

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import tsheppard01.exceptions.DataShapeException

/**
  * Trait for converting a string represented record to an avro
  * record
  */
trait StringToAvroRecordConverter {

  /**
    * Method to convert a string to and avro record
    *
    * @param record The record to convert
    * @param schema The schema to use in the conversion
    */
  def convert(record: String, schema: Schema): GenericData.Record
}

/**
  * Class to convert a csv record to avro
  */
class CsvStringToAvroConverter extends StringToAvroRecordConverter {

  private val DELIMITER = ","

  /**
    * Method to convert a csv record to a avro using the schema provided
    *
    * @param record The record to convert
    * @param schema The schema to use in the conversion
    */
  override def convert(record: String, schema: Schema): GenericData.Record = {

    val avroRecord = new GenericData.Record(schema)
    val delimitedFields = record.split(s"\\$DELIMITER", -1)

    if (schema.getFields.size() != delimitedFields.length)
      throw new DataShapeException(
        "Number of fields in csv record not equal to number of fields in schema provided")

    val delimitedFieldsIterator = delimitedFields.iterator

    List
      .range(0, delimitedFields.length)
      .foreach { index =>
        avroRecord.put(index, delimitedFieldsIterator.next())
      }
    avroRecord
  }
}
