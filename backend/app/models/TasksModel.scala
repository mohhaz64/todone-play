package models

import javax.inject._
import scala.collection.concurrent
import todone.data._

import scala.collection.concurrent.TrieMap

@Singleton
class TasksModel() {
  val currentId = new java.util.concurrent.atomic.AtomicInteger(4)
  val taskStore = TrieMap[Id, Task](
    Id(0) -> Task(
      State.open,
      "Play with the ToDone interface",
      """Right now many things in the application are not working.
        |Our task is to make it work, and the first step to that is
        |finding what is needs to be fixed.""".stripMargin,
      Some(Project("todone")),
      Tags(List(Tag("scalabridge"), Tag("frontend")))
    ),
    Id(1) -> Task(
      State.open,
      "Learn how to use the web developer tools",
      """The web developer tools are one of the most useful tools for
        |debugging problems between the frontend and backend. We need
        |open up the web developer tools and look at the network pane,
        |where we'll find requests from the frontend that the backend
        |is not properly responding to.""".stripMargin,
      Some(Project("todone")),
      Tags(List(Tag("scalabridge"), Tag("frontend")))
    ),
    Id(2) -> Task(
      State.open,
      "Implement functionality to close a completed task",
      """The close button is probably the simplest bit of functionality
        |we can implement. (Hopefully you discovered the close button
        |doesn't work.) Let's do that now! The worksheets have more
        |details.""".stripMargin,
      Some(Project("todone")),
      Tags(List(Tag("scalabridge"), Tag("backend")))
    ),
    Id(3) -> Task(
      State.open,
      "Have a break!",
      "We've done a lot. Time for a break.",
      None,
      Tags(List(Tag("chillout")))
    )
  )

  def nextId(): Id = {
    val id = currentId.getAndIncrement()
    Id(id)
  }

  /** Get all the tasks */
  def tasks: Tasks = {
    val sortedTasks: List[(Id, Task)] =
      taskStore.toArray.sortInPlaceBy { case (id, _) => id}.toList
    Tasks(sortedTasks)
  }

  /** Get all the tags */
  def tags: Tags = {
    val tasksMapToList: List[(Id, Task)] = this.tasks.tasks
    val tags: List[Tag] = tasksMapToList.flatMap(task => task._2.tags.tags).distinct
    Tags(tags)
  }

  def projects: Projects = {
    val tasksMapToList: List[(Id, Task)] = this.tasks.tasks
    val projects: List[Project] = tasksMapToList.flatMap{case (id, task) => task.project.toList}.distinct
    Projects(projects)
  }

  /** Create a new task */
  def create(task: Task): Id = {
    val id = nextId()
    taskStore.addOne(id -> task)
    id
  }

  def closeTask(id: Id): Option[Task] =
    this.update(id)((t:Task) => t.close)

  def update(id: Id)(f: Task => Task): Option[Task] =
    taskStore.updateWith(id)(opt => opt.map(f))
}
