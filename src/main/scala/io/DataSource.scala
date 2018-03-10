package io

import scala.util.Random

trait DataSource {

  def nextRecord(): String
}

class CsvGeneratorDataSource extends DataSource {
  override def nextRecord(): String =
    s"a,b,c,d,e,${Random.nextPrintableChar()}"
}
