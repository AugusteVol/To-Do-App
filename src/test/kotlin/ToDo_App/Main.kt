import java.time.Duration
import java.time.Instant

// filter
enum class TaskFilter {
    ALL,
    ACTIVE,
    DONE
}

// task data class
data class Task(
    val id: Int,
    val description: String,
    var isCompleted: Boolean = false
)

class TodoApp {
    private val tasks = mutableListOf<Task>()
    private var nextId = 1
    private var actionsPerformed = 0
    private var deletedTasksCount = 0

    fun addTask(description: String) {
        if (description.isBlank()) {
            println("Uzduoties aprasymas negali buti tuscias")
            return
        }
        val task = Task(nextId++, description)
        tasks.add(task)
        actionsPerformed++
        println("Prideta uzduotis: \"${task.description}\" (ID: ${task.id})")
    }

    fun completeTask(id: Int) {
        val task = tasks.find { it.id == id }
        if (task == null) {
            println("Uzduotis su ID $id nerasta.")
            return
        }
        if (task.isCompleted) {
            println("Uzduotis \"${task.description}\" jau buvo atlikta.")
            return
        }
        task.isCompleted = true
        actionsPerformed++
        println("Uzduotis ID $id pazymeta kaip atlikta")
    }

    // delete one task via ID
    fun deleteTask(id: Int) {
        val task = tasks.find { it.id == id }
        if (task == null) {
            println("Nepavyko istrinti: uzduotis su ID $id nerasta.")
            return
        }

        tasks.remove(task)
        actionsPerformed++
        deletedTasksCount++
        println("Istrinta uzduotis: \"${task.description}\" (ID: $id)")
    }

    // task print with filter
    fun printTaskList(filter: TaskFilter = TaskFilter.ALL) {
        val filteredTasks = when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.ACTIVE -> tasks.filter { !it.isCompleted }
            TaskFilter.DONE -> tasks.filter { it.isCompleted }
        }

        val filterTitle = when (filter) {
            TaskFilter.ALL -> "VISOS"
            TaskFilter.ACTIVE -> "NEATLIKTOS"
            TaskFilter.DONE -> "ATLIKTOS"
        }

        println("\nUZDUOCIU SARASAS [$filterTitle]")
        if (filteredTasks.isEmpty()) {
            println(" Sarasas tuscias.")
        } else {
            filteredTasks.forEach { task ->
                val status = if (task.isCompleted) "(Atlikta)" else "(Vykdoma)"
                println(" ${task.id}. $status ${task.description}")
            }
        }
        println("---")
    }

    fun printFinalReport(duration: Duration) {
        println("\n---")
        println("REZULTATAI")
        println("---")

        printTaskList(TaskFilter.ALL)

        val completedCount = tasks.count { it.isCompleted }
        val pendingCount = tasks.size - completedCount

        println("\n STATISTIKA:")
        println(" * Esamu uzduociu kiekis : ${tasks.size}")
        println(" * Atlikta               : $completedCount")
        println(" * Neatlikta             : $pendingCount")
        println(" * Istrintu uzduociu sk. : $deletedTasksCount")
        println(" * Atliktu veiksmu sk.   : $actionsPerformed")
        println(" * Veikimo trukme        : ${duration.toSeconds()} s ir ${duration.toMillis()} ms ")
        println("---")
    }
    //cuts the description length
    fun getShortDescription(id: Int, maxLength: Int = 10): String {
        val task = tasks.find { it.id == id }
        val desc = task?.description ?: return "uzd neegzistuoja"

        return try {
            if (desc.length > maxLength) desc.substring(0, maxLength) else desc
        } catch (e: Exception) {
            "klaida, help"
        }
    }
}

fun main() {
    val startTime = Instant.now()
    val app = TodoApp()

    println("TODO PROGRAMELE\n")

    // tasks
    app.addTask("Nusipirkti pieno")
    app.addTask("Nusiplauti rankas")
    app.addTask("Eiti pasivaikscioti")
    app.addTask("Pavedzioti suniuka")
    app.addTask("Gauti 10")
    app.addTask("Neklausti nieko apie lambda funkcijas")

    // marks task as complete
    app.completeTask(2)
    app.completeTask(1)

    // filters with enum class
    println("\nFILTRAVIMAS")
    app.printTaskList(TaskFilter.ALL)
    // active tasks
    app.printTaskList(TaskFilter.ACTIVE)
    // done tasks
    app.printTaskList(TaskFilter.DONE)

    //decription
    println("\nDESCRIPTION")
    println(app.getShortDescription(3, 15))

    // task removal
    println("\nTRYNIMAS")
    app.deleteTask(3)
    app.deleteTask(99) // ID doesnt exist

    // stops timer
    val endTime = Instant.now()
    val duration = Duration.between(startTime, endTime)

    app.printFinalReport(duration)
}