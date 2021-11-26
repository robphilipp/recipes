package com.digitalcipher.repositories

import com.digitalcipher.repositories.dao.RecipeDao
import com.digitalcipher.services.domain.Recipe
import com.mongodb.client.model.InsertOneOptions
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq

class RecipeRepo(mongoClient: CoroutineClient) {
    private val collection = mongoClient.getDatabase("recipeBook").getCollection<RecipeDao>()

    suspend fun recipes(): Result<List<Recipe>> {
        return kotlin.runCatching {
            collection.find().toList().map { it.asRecipe() }
        }
    }

    suspend fun add(recipe: Recipe): Result<Boolean> {
        return kotlin.runCatching {
            if (collection.countDocuments(RecipeDao::name eq recipe.name) > 0) {
                return Result.failure(NonUniqueName("Recipe already exists", recipe.name))
            }
            collection.insertOne(RecipeDao.from(recipe)).wasAcknowledged()
        }
    }

    suspend fun delete(name: String): Result<Long> {
        return kotlin.runCatching {
            collection.deleteMany(RecipeDao::name eq name).deletedCount
        }
    }
}

@Serializable
data class NonUniqueName(val error: String, val recipeName: String): Exception(error)