package com.digitalcipher

import com.digitalcipher.plugins.configureHTTP
import com.digitalcipher.plugins.configureMonitoring
import com.digitalcipher.plugins.configureRouting
import com.digitalcipher.plugins.configureSerialization
import com.digitalcipher.repositories.ShoppingListRepo
import com.digitalcipher.repositories.dao.ShoppingListItem
import io.ktor.application.*
import io.ktor.config.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val repositories = createRepositories(environment.config)

    configureRouting(repositories.shoppingListRepo)
    configureHTTP()
    configureMonitoring()
    configureSerialization()
}

data class Repositories(val shoppingListRepo: ShoppingListRepo)

fun createRepositories(config: ApplicationConfig): Repositories {
    val mongoUrl = config.propertyOrNull("ktor.mongo.connectionUrl")?.getString() ?: "oops"
    val client = KMongo.createClient(mongoUrl).coroutine

    // shopping list repo
    val shoppingListRepo = ShoppingListRepo(client.getDatabase("shoppingList").getCollection())

    return Repositories(shoppingListRepo)
}
