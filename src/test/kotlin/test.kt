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
}