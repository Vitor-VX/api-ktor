package victor.server.kt.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import victor.server.kt.models.Priority
import victor.server.kt.models.Task
import victor.server.kt.models.TaskRepository

fun Task.taskAsRow() = """
    <tr>
        <td>$name</td><td>$description</td><td>$priority</td>
    </tr>
    """.trimIndent()

fun List<Task>.tasksAsTable() = this.joinToString(
    prefix = "<table rules=\"all\">",
    postfix = "</table>",
    separator = "\n",
    transform = Task::taskAsRow
)

fun Application.configureRouting() {
    routing {
        val tasks = TaskRepository.allTasks()

        staticResources("/task-ui", "task-ui")

        post("/tasks") {
            val formContent = call.receiveParameters()
            val params = Triple(
                formContent["name"] ?: "",
                formContent["description"] ?: "",
                formContent["priority"] ?: ""
            )

            if (params.toList().any { it.isEmpty() }) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            try {
                val priority = Priority.valueOf(params.third)
                TaskRepository.addTask(
                    Task(
                        params.first,
                        params.second,
                        priority
                    )
                )

                call.respond(HttpStatusCode.NoContent)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/tasks/getByTask/{priority}") {
            val priorityAsText = call.parameters["priority"]
            if (priorityAsText == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val priority = Priority.valueOf(priorityAsText)
                val tasksSearch = TaskRepository.tasksByPriority(priority)
                if (tasks.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = tasksSearch.tasksAsTable()
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/add") {
            val taskAdd = Task(
                "Test",
                "Test description",
                Priority.Medium
            )
            TaskRepository.addTask(taskAdd)

            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )
        }

        get("/tasks") {
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )
        }

        get("/task") {
            val html = """
                <h3>TODO:</h3>
                <ol>
                    <li>A table of all the tasks</li>
                    <li>A form to submit new tasks</li>
                </ol>
            """.trimIndent()
            val type = ContentType.parse("text/html")

            call.respondText(
                contentType = type,
                text = html
            )
        }
    }
}




/*
fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText {
                "Error local server: ${cause.message}"
            }
        }
    }

    routing {
        staticResources("/app", "mycontent")

        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }

        get("/app") {
            val html = "<h2>Hello Victor</h2>"
            val type = ContentType.parse("text/html")
            call.respondText(html, type)
        }
    }
}
*/