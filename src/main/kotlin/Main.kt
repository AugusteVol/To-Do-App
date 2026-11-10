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

// Unit testing
/*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TodoAppTest {

    private lateinit var app: TodoApp

    @BeforeEach
    fun setUp() {
        app = TodoApp()
    }

    @Test
    fun `test add valid task increases count and assigns id`() {
        app.addTask("Pienas")
        assertEquals(1, app.getTaskCount())
        assertEquals("Pienas", app.getShortDescription(1, 20))
    }

    @Test
    fun `test add blank description does not add task`() {
        app.addTask("   ")
        assertEquals(0, app.getTaskCount())
    }

    @Test
    fun `test complete task marks task as completed`() {
        app.addTask("Skaityti knyga")
        app.completeTask(1)
        assertTrue(app.isTaskCompleted(1))
    }

    @Test
    fun `test complete non existent task does not crash`() {
        app.completeTask(99)
        assertEquals(0, app.getTaskCount())
    }

    @Test
    fun `test complete already completed task no errors`() {
        app.addTask("Testas")
        app.completeTask(1)
        assertTrue(app.isTaskCompleted(1))

        // completing it a second time should not throw an exception
        app.completeTask(1)
        assertTrue(app.isTaskCompleted(1))
    }

    @Test
    fun `test delete task removes it from list`() {
        app.addTask("Išmesti šiukšles")
        assertEquals(1, app.getTaskCount())

        app.deleteTask(1)
        assertEquals(0, app.getTaskCount())
    }

    @Test
    fun `test delete non existent task does not crash`() {
        app.addTask("Darbas")
        app.deleteTask(99)
        assertEquals(1, app.getTaskCount())
    }

    @Test
    fun `test get short description cuts long text`() {
        app.addTask("Labai ilgas užduoties aprašymas čia")
        val shortDesc = app.getShortDescription(1, 5)
        assertEquals("Labai", shortDesc)
    }

    @Test
    fun `test get short description returns full text when shorter than limit`() {
        app.addTask("Trumpai")
        val shortDesc = app.getShortDescription(1, 20)
        assertEquals("Trumpai", shortDesc)
    }

    @Test
    fun `test get short description for non existent task returns error message`() {
        val result = app.getShortDescription(999, 10)
        assertEquals("uzd neegzistuoja", result)
    }
}*/