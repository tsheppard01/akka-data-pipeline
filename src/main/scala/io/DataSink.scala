package io

import org.apache.avro.generic.GenericData

trait DataSink {

  def writeRecord(record: GenericData.Record)
}

class DummyDataSink extends DataSink {
  override def writeRecord(record: GenericData.Record): Unit = {
    println("dummy wrote the record")
  }
}
