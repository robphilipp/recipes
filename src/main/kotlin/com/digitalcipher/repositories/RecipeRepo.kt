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

    suspend fun update(recipe: Recipe): Result<Long> {
        return kotlin.runCatching {
            val originals = collection.find(RecipeDao::name eq recipe.name).toList()
            if (originals.size != 1) {
//            if (collection.countDocuments(RecipeDao::name eq recipe.name) == 0L) {
                return Result.failure(RecipeNotFound("Recipe not found", recipe.name))
            }
            val updated = RecipeDao.asModified(recipe, originals[0].createdOn)
            collection.updateOne(RecipeDao::name eq recipe.name, updated).modifiedCount
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

@Serializable
data class RecipeNotFound(val error: String, val recipeName: String): Exception(error)
