package com.digitalcipher.repositories.dao

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemDao(val description: String, val priority: Int) {
    val id: Int = description.hashCode()

    companion object {
        const val path = "/shoppingList"
    }
}