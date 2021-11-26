package com.digitalcipher.services

import com.digitalcipher.repositories.ShoppingListRepo
import com.digitalcipher.repositories.dao.ShoppingListItemDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(ShoppingListService::class.java.name)

class ShoppingListService(val repo: ShoppingListRepo) {
    suspend fun items(): Result<List<ShoppingListItemDao>> {
        return repo
            .items()
            .onFailure {
                logger.error("Failed to retrieve shopping-list items; error: {}", it.message)
            }
    }

    suspend fun add(item: ShoppingListItemDao): Result<Boolean> {
        return repo
            .add(item)
            .onFailure {
                logger.error("Failed to write shopping-list item; item: {}; error: {}", item.toString(), it.message)
            }
    }

    suspend fun delete(id: Int): Result<Unit> {
        return repo
            .delete(id)
            .onFailure {
                logger.error("Failed to delete shopping-list item; item_id: {}; error: {}", id, it.message)
            }
    }
}