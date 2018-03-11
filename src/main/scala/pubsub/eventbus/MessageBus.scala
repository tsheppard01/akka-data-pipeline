package pubsub.eventbus

import akka.actor.ActorRef
import akka.event.{ActorEventBus, LookupClassification}
import pubsub.eventbus.MessageBus.MessageEvent

trait MyMessageBus extends ActorEventBus with LookupClassification {
  override type Event = MessageBus.MessageEvent
  override type Classifier = String

  override protected def mapSize(): Int = 128
}

class MessageBus extends MyMessageBus {

  override protected def classify(event: MessageEvent): String = {
    println("Classifying event")
    event.stage
  }

  override protected def publish(event: MessageEvent, subscriber: ActorRef): Unit =
    subscriber ! event.payload
}

object MessageBus{
  final case class MessageEvent(stage: String, payload: Any)
}