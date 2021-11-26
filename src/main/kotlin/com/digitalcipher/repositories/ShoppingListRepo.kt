package com.digitalcipher.repositories

import com.digitalcipher.repositories.dao.ShoppingListItemDao
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(ShoppingListRepo::class.java.name)

class ShoppingListRepo(private val collection: CoroutineCollection<ShoppingListItemDao>) {
    suspend fun items(): Result<List<ShoppingListItemDao>> {
        return kotlin.runCatching {
            collection.find().toList()
        }
    }

    suspend fun add(item: ShoppingListItemDao): Result<Boolean> {
        return kotlin.runCatching {
            val result = collection.insertOne(item)
            result.wasAcknowledged()
        }
//        return try {
//            val result = collection.insertOne(item)
//            result.wasAcknowledged()
//        } catch(e: MongoWriteException) {
//            logger.error("Failed to write shopping list item; item: {}; error: {}", item.toString(), e.message)
//            false
//        }
    }

    suspend fun delete(id: Int): Result<Unit> {
        return kotlin.runCatching {
            collection.deleteOne(ShoppingListItemDao::id eq id)
        }
    }
}