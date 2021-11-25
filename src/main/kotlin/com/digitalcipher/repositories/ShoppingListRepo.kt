package com.digitalcipher.repositories

import com.digitalcipher.repositories.dao.ShoppingListItem
import io.ktor.application.*
import io.ktor.request.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class ShoppingListRepo(private val collection: CoroutineCollection<ShoppingListItem>) {
    suspend fun items(): List<ShoppingListItem> {
        return collection.find().toList()
    }

    suspend fun add(item: ShoppingListItem): Boolean {
        val result = collection.insertOne(item)
        return result.wasAcknowledged()
    }

    suspend fun delete(id: Int) {
        collection.deleteOne(ShoppingListItem::id eq id)
    }
}