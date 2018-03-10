package masking

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData

trait AvroConverter {

  def convertRecordToAvro(record: String, schema: Schema): GenericData.Record
}

class DelimitedAvroConverter(delimter: String) extends AvroConverter {

  override def convertRecordToAvro(record: String, schema: Schema): GenericData.Record = {

    val avroRecord = new GenericData.Record(schema)
    val delimitedFields = record.split(s"\\$delimter", -1)
    val delimitedFieldsIterator = delimitedFields.iterator

    List.range(0,delimitedFields.length)
      .foreach{ index =>
        avroRecord.put(index, delimitedFieldsIterator.next())
      }
    avroRecord
  }
}