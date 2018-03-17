package tsheppard01.eventbus

import akka.actor.ActorRef
import akka.event.{ActorEventBus, LookupClassification}
import tsheppard01.eventbus.PipelineMessageBus.MessageEvent

trait MessageBus extends ActorEventBus with LookupClassification {

  override type Event = PipelineMessageBus.MessageEvent

  override type Classifier = String

  override protected def mapSize(): Int = 128
}

class PipelineMessageBus extends MessageBus {

  override protected def classify(event: MessageEvent): String = {
    event.stage
  }

  override protected def publish(event: MessageEvent, subscriber: ActorRef): Unit =
    subscriber ! event.payload
}

object PipelineMessageBus{
  final case class MessageEvent(stage: String, payload: Any)
}