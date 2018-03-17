package tsheppard01.io

import scala.util.Random

/**
  * Trait to get a string record from source
  */
trait DataSource {

  def nextRecord(): String
}

/**
  * Class to generate csv data as a source
  */
class CsvGeneratorDataSource extends DataSource {

  private val MAX_FIELD_LENGTH = 5
  private val NUM_FIELDS = 6

  /**
    * Method to generate the next record
    */
  override def nextRecord(): String = {
    val fields = for {
      _ <- 1 to NUM_FIELDS
      nextField = (for {
        _ <- 1 to Random.nextInt(MAX_FIELD_LENGTH)
      } yield Random.nextPrintableChar()
        ).mkString("")
    } yield nextField

    fields.mkString(",")
  }
}