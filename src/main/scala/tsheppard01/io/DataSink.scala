package tsheppard01.io

import org.apache.avro.generic.GenericData
import tsheppard01.logger.Logger

/**
  * Trait for a outputting avro records
  */
trait DataSink {

  def writeRecord(record: GenericData.Record)
}

/**
  * Class that writes records to log
  */
class LogDataSink extends DataSink with Logger {

  /**
    * Method to write records to log
    *
    * @param record The record to output
    */
  override def writeRecord(record: GenericData.Record): Unit = {
    logger.info(s"Wrote: ${record.toString}")
  }
}
