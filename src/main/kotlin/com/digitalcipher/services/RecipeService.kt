package com.digitalcipher.services

import com.digitalcipher.repositories.NonUniqueName
import com.digitalcipher.repositories.RecipeRepo
import com.digitalcipher.services.domain.Recipe
import org.slf4j.LoggerFactory


class RecipeService(val repo: RecipeRepo) {
    suspend fun recipes(): Result<List<Recipe>> {
        return repo
            .recipes()
            .onFailure { logger.error("Unable to retrieve recipes; error: {}", it.message) }
    }

    suspend fun add(recipe: Recipe): Result<Boolean> {
        return repo
            .add(recipe)
            .onFailure { logger.error("Unable to add recipe; name: {}; error: {}", recipe.name, it.message) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RecipeRepo::class.java.name)
    }
}