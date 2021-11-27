package com.digitalcipher.plugins

import com.digitalcipher.repositories.NonUniqueName
import com.digitalcipher.repositories.RecipeNotFound
import com.digitalcipher.repositories.dao.ShoppingListItemDao
import com.digitalcipher.rest.mediation.NewUpdateRecipeMo
import com.digitalcipher.rest.mediation.RecipeMo
import com.digitalcipher.services.RecipeService
import com.digitalcipher.services.ShoppingListService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting(service: ShoppingListService, recipeService: RecipeService) {
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

        route("/recipes") {
            get {
                recipeService.recipes()
                    .onSuccess { recipes -> call.respond(recipes.map { RecipeMo.from(it)}) }
                    .onFailure { call.respond(HttpStatusCode.InternalServerError, FailedException(it.message ?: "")) }
            }

            post {
                recipeService
                    .add((call.receive() as NewUpdateRecipeMo).asRecipe())
                    .onSuccess { acknowledged ->
                        if (acknowledged) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.BadRequest)
                        }
                    }
                    .onFailure {
                        if (it is NonUniqueName) {
                            call.respond(HttpStatusCode.BadRequest, it)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError, it)
                        }
                    }
            }

            put {
                recipeService
                    .update((call.receive() as NewUpdateRecipeMo).asRecipe())
                    .onSuccess { modified ->
                        if (modified > 0) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.BadRequest)
                        }
                    }
                    .onFailure {
                        if (it is RecipeNotFound) {
                            call.respond(HttpStatusCode.BadRequest, it)
                        } else {
                            call.respond(HttpStatusCode.InternalServerError, it)
                        }
                    }
            }

            delete("/{recipeName}") {
                val name = call.parameters["recipeName"]?: error("Invalid delete request")
                recipeService
                    .delete(name)
                    .onSuccess { call.respond(DeletedRecipes(name, it)) }
                    .onFailure { call.respond(HttpStatusCode.InternalServerError) }
            }
        }
    }
}

@Serializable
data class DeletedRecipes(val recipeName: String, val numDeleted: Long)

@Serializable
data class FailedException(val error: String): Exception(error)