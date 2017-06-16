package fun.scala.store

import java.util.UUID

import fun.scala.store.generic._
import org.scalatest.{Matchers, WordSpec}

class GenericSpec extends WordSpec with Matchers {

  object DoorAggregate {

    // state
    case class Door(isLocked: Boolean, knocks: Int)

    // commands
    case class Knock() extends Command[Door]
    case class Lock() extends Command[Door]
    case class Unlock() extends Command[Door]

    // events
    case class Knocked() extends Event[Door] {
      val typeName = "knocked"
    }
    case class Locked() extends Event[Door] {
      val typeName = "locked"
    }
    case class Unlocked() extends Event[Door] {
      val typeName = "unlocked"
    }

    val commandHandler: Aggregate.CommandHandler[Door] = (snapshot, commmand) => commmand match {
      case Knock() => (() => (), List(Knocked()))
      case Lock() => (() => (), List(Locked()))
      case Unlock() => (() => (), List(Unlocked()))
    }

    val eventHandler: Aggregate.EventHandler[Door] = (snapshot, event) => event match {
        case Knocked()  => snapshot.copy(state = snapshot.state.copy(knocks = snapshot.state.knocks + 1))
        case Locked()   => snapshot.copy(state = snapshot.state.copy(isLocked = true))
        case Unlocked() => snapshot.copy(state = snapshot.state.copy(isLocked = false, knocks = 0))
      }

    // aggregate id
    case class DoorAggregateId(doorId: String = "") extends AggregateId[Door]("door", doorId)

    val factory: Factory[Door] = Factory[Door](
      Door(isLocked = true, knocks = 0),
      DoorAggregateId("00000000-0000-0000-0000-000000000000"),
      DoorAggregateId,
      commandHandler,
      eventHandler
    )

    val doorMemoryStore = new RuntimeEventStore[Door]()
    val doorRepository = Repository[Door](doorMemoryStore, factory)

  }

  import DoorAggregate._

  "aggregate id" should {
    "generate id from seed" in {
      DoorAggregateId(AggregateIdString("something")) should be (DoorAggregateId(AggregateIdString("something")))
      DoorAggregateId(AggregateIdString("something else")) should not be DoorAggregateId(AggregateIdString("something"))
    }
    "use passed uuid string representation" in {
      DoorAggregateId("15fd7a3a-d6e3-37a8-b07f-8b51da64880e").id.toString should be ("15fd7a3a-d6e3-37a8-b07f-8b51da64880e")
    }
    "generate random uuid when passed malformed uuid string representation" in {
      DoorAggregateId("a").id.toString should not be DoorAggregateId("a").id.toString
    }
    "accept uuid representation without dashes" in {
      DoorAggregateId("15fd7a3ad6e337a8b07f8b51da64880e").id.toString should be ("15fd7a3a-d6e3-37a8-b07f-8b51da64880e")
    }
  }

  "aggregate" should {
    "not appear in repository when does not have any events against it" in {
      val newDoor = doorRepository.create
      doorRepository.getById(newDoor.id) should be(None)
    }
    "updates repository as soon as receives new command" in {
      val newDoor = doorRepository.create
      val updatedDoor = newDoor & Unlock()
      val doorFromRepository = doorRepository.getById(newDoor.id)
      doorFromRepository.nonEmpty should be(true)
//      doorFromRepository.get.events should be(updatedDoor.events)
    }
  }

  "repository" should {
    "return none when asked for non existing aggregate" in {
      doorRepository.getById(DoorAggregateId()) should be(None)
    }
    "create initial aggregate" in {
      val newDoor = doorRepository.create
      newDoor.state should be(factory.initialState)
    }
    "store initial aggregate" in {
      val newDoor = doorRepository.create
//      val savedDoor = doorRepository.save(newDoor)
//      doorMemoryStore.streams should be(Map(savedDoor.id.toStreamId -> (savedDoor.events, savedDoor.version)))
    }
    "apply commands to the aggregate" in { // knock, knock, knock, unlock, knock, lock
      val closedDoor = doorRepository.create & Knock() & Knock() & Knock()
      closedDoor.state should be(Door(isLocked = true, knocks = 3))
      val openedDoor = closedDoor & Unlock()
      openedDoor.state should be(Door(isLocked = false, knocks = 0))
      val closedDoorAgain = openedDoor & Knock() & Lock()
      closedDoorAgain.state should be(Door(isLocked = true, knocks = 1))
    }
    "store events in event store" in {
      val door = doorRepository.create & Knock() & Unlock() & Knock() & Knock() & Lock()
//      val savedDoor = doorRepository.save(door)
//      doorMemoryStore.streams(savedDoor.id.toStreamId) should be((savedDoor.events, savedDoor.version))
//      doorRepository.getById(savedDoor.id).contains(savedDoor)
    }
    "append new events to already saved aggregate" in {
      val door = doorRepository.create & Knock() & Knock()
//      val savedDoor = doorRepository.save(door)
//      val existing = doorRepository.getById(savedDoor.id).get
//      existing.id should be(savedDoor.id)
//      val updated = existing & Knock() & Knock()
//      val savedUpdated = doorRepository.save(updated)
//      savedUpdated.id should be(savedDoor.id)
//      savedUpdated.events should be(savedDoor.events :+ Knocked() :+ Knocked())
    }
  }

}
