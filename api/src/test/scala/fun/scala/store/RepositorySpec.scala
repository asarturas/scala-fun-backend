package fun.scala.store

import java.util.UUID

import fun.scala.store.generic._
import org.scalatest.{FlatSpec, Matchers}

class RepositorySpec extends FlatSpec with Matchers {

  object DoorAggregate {

    // state
    case class Door(isLocked: Boolean, knocks: Int)

    // init aggregate
    implicit val DoorInit = Snapshot[Door](
      id = DoorAggregateId(UUID.fromString("00000000-0000-0000-0000-000000000000")),
      Door(isLocked = true, knocks = 0)
    )

    // commands
    case class Knock() extends Command[Door]
    case class Lock() extends Command[Door]
    case class Unlock() extends Command[Door]

    // events
    case class Knocked() extends Event[Door]
    case class Locked() extends Event[Door]
    case class Unlocked() extends Event[Door]


    // aggregate id
    case class DoorAggregateId(id: UUID) extends AggregateId[Door] {
      def toStreamId: StreamId = StreamId("door-" + id.toString)
    }

    // aggregate -> define the event replay and command application
    case class DoorAggregate(override val id: AggregateId[Door], override val version: VersionNumber, init: List[Event[Door]])
      extends Aggregate[Door](id, version, init, DoorInit) {
      def replay(snapshot: Snapshot[Door], event: Event[Door]): Snapshot[Door] = event match {
        case Knocked()  => snapshot.copy(state = snapshot.state.copy(knocks = snapshot.state.knocks + 1))
        case Locked()   => snapshot.copy(state = snapshot.state.copy(isLocked = true))
        case Unlocked() => snapshot.copy(state = snapshot.state.copy(isLocked = false, knocks = 0))
      }

      def apply(command: Command[Door]): DoorAggregate = command match {
        case Knock() => DoorAggregate(id, version, events :+ Knocked())
        case Lock() => DoorAggregate(id, version, events :+ Locked())
        case Unlock() => DoorAggregate(id, version, events :+ Unlocked())
      }
    }

    // implicit link to constructor
    implicit def doorAggregateConstructor = DoorAggregate

    // implicit link to aggregate id generator
    implicit def doorAggregateIdGenerator = () => DoorAggregateId(UUID.randomUUID)

  }

  import DoorAggregate._
  import NumericVersion._

  val doorMemoryStore = new RuntimeEventStore(() => DoorAggregateId(UUID.randomUUID), NumericVersion())
  val doorRepository = Repository(doorMemoryStore)

  it should "return none when asked for non existing aggregate" in {
    doorRepository.getById(DoorAggregateId(UUID.randomUUID)) should be(None)
  }
  it should "create initial aggregate" in {
    val newDoor = doorRepository.create
    newDoor.snapshot should be(DoorInit)
  }
  it should "store initial aggregate" in {
    val newDoor = doorRepository.create
    val savedDoor = doorRepository.save(newDoor)
    doorMemoryStore.streams should be(Map(savedDoor.id.toStreamId -> (savedDoor.events, savedDoor.version)))
  }

  it should "apply commands to the aggregate" in { // knock, knock, knock, unlock, knock, lock
    val closedDoor = doorRepository.create & Knock() & Knock() & Knock()
    closedDoor.state should be(Door(isLocked = true, knocks = 3))
    val openedDoor = closedDoor & Unlock()
    openedDoor.state should be(Door(isLocked = false, knocks = 0))
    val closedDoorAgain = openedDoor & Knock() & Lock()
    closedDoorAgain.state should be(Door(isLocked = true, knocks = 1))
  }

  it should "store events in event store" in {
    val door = doorRepository.create & Knock() & Unlock() & Knock() & Knock() & Lock()
    val savedDoor = doorRepository.save(door)
    doorMemoryStore.streams(savedDoor.id.toStreamId) should be((savedDoor.events, savedDoor.version))
    doorRepository.getById(savedDoor.id).contains(savedDoor)
  }

  it should "append new events to already saved aggregate" in {
    val door = doorRepository.create & Knock() & Knock()
    val savedDoor = doorRepository.save(door)
    val existing = doorRepository.getById(savedDoor.id).get
    existing.id should be(savedDoor.id)
    val updated = existing & Knock() & Knock()
    val savedUpdated = doorRepository.save(updated)
    savedUpdated.id should be(savedDoor.id)
    savedUpdated.events should be(savedDoor.events :+ Knocked() :+ Knocked())
  }

  it should "ignore "

}
