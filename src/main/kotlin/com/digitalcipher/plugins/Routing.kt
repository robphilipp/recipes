package com.digitalcipher.plugins

import com.digitalcipher.repositories.ShoppingListRepo
import com.digitalcipher.repositories.dao.ShoppingListItem
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

fun Application.configureRouting(repo: ShoppingListRepo) {
//fun Application.configureRouting(collection: CoroutineCollection<ShoppingListItem>) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }

        // REST API
        route(ShoppingListItem.path) {
            get {
                call.respond(repo.items())
            }

            post {
                val acknowledged = repo.add(call.receive())
                if (acknowledged) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                repo.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }

    }
}

@Serializable
data class AddedId(val id: Int)