package com.digitalcipher.plugins

import com.digitalcipher.repositories.dao.ShoppingListItemDao
import com.digitalcipher.services.ShoppingListService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting(service: ShoppingListService) {
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
        route(ShoppingListItemDao.path) {
            get {
                service.items().onSuccess { call.respond(it) }
            }

            post {
                service
                    .add(call.receive())
                    .onSuccess { acknowledged -> if (acknowledged) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest) }
                    .onFailure { call.respond(HttpStatusCode.InternalServerError) }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                service
                    .delete(id)
                    .onSuccess { call.respond(HttpStatusCode.OK) }
                    .onFailure { call.respond(HttpStatusCode.InternalServerError) }
            }
        }

    }
}

@Serializable
data class AddedId(val id: Int)