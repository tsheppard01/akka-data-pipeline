package tsheppard01.logger

import org.slf4j.LoggerFactory

/**
  * Trait to supply logger, to be used as mixin
  */
trait Logger {

  val logger = LoggerFactory.getLogger(this.getClass.getSimpleName)
}
