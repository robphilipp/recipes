package com.digitalcipher

import com.digitalcipher.plugins.configureHTTP
import com.digitalcipher.plugins.configureMonitoring
import com.digitalcipher.plugins.configureRouting
import com.digitalcipher.plugins.configureSerialization
import com.digitalcipher.repositories.ShoppingListRepo
import com.digitalcipher.services.ShoppingListService
import io.ktor.application.*
import io.ktor.config.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val logger = LoggerFactory.getLogger("Application.module")

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    logger.info("application.module called")
    val repositories = createRepositories(environment.config)
    val services = createServices(repositories)

//    configureRouting(repositories.shoppingListRepo)
    configureRouting(services.shoppingList)
    configureHTTP()
    configureMonitoring()
    configureSerialization()
}

data class Repositories(val shoppingList: ShoppingListRepo)

fun createRepositories(config: ApplicationConfig): Repositories {
    val mongoUrl = config.propertyOrNull("ktor.mongo.connectionUrl")?.getString() ?: "oops"
    val client = KMongo.createClient(mongoUrl).coroutine

    // shopping list repo
    val shoppingListRepo = ShoppingListRepo(client.getDatabase("shoppingList").getCollection())

    return Repositories(shoppingListRepo)
}

data class Services(val shoppingList: ShoppingListService)

fun createServices(repositories: Repositories): Services {
    val shoppingListService = ShoppingListService(repositories.shoppingList)
    return Services(shoppingListService)
}