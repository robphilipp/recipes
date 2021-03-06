package com.digitalcipher.repositories.dao

import com.digitalcipher.services.domain.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.Clock
import java.time.Instant

@Serializable
data class RecipeDao(
    val name: String,
    val createdOn: Long,
    val modifiedOn: Long? = null,
    val ingredients: List<IngredientDao>,
    val steps: List<StepDao>
) {
    fun asRecipe() = Recipe(
        name = name,
        createdTimestamp = Timestamp.from(Instant.ofEpochMilli(createdOn)),
        modifiedTimestamp = if (modifiedOn != null) Timestamp.from(Instant.ofEpochMilli(modifiedOn)) else null,
        ingredients = ingredients.map { it.asIngredient() },
        steps = steps.map { it.asStep() }
    )

    companion object {
        fun from(recipe: Recipe) = RecipeDao(
            name = recipe.name,
            // set the created timestamp if it doesn't already exist
            createdOn = recipe.createdTimestamp?.toInstant()?.toEpochMilli() ?: Instant.now(Clock.systemUTC()).toEpochMilli(),
            modifiedOn = recipe.modifiedTimestamp?.toInstant()?.toEpochMilli(),
            ingredients = recipe.ingredients.map { IngredientDao.from(it) },
            steps = recipe.steps.map { StepDao.from(it) }
        )
        fun asModified(recipe: Recipe, createdOn: Long?) = RecipeDao(
            name = recipe.name,
            createdOn = createdOn ?: recipe.createdTimestamp?.toInstant()?.toEpochMilli() ?: Instant.now(Clock.systemUTC()).toEpochMilli(),
            modifiedOn = Instant.now(Clock.systemUTC()).toEpochMilli(),
            ingredients = recipe.ingredients.map { IngredientDao.from(it) },
            steps = recipe.steps.map { StepDao.from(it) }
        )
    }
}

@Serializable
data class StepDao(val title: String? = null, val text: String) {
    fun asStep() = Step(title = title, text = text)

    companion object {
        fun from(step: Step) = StepDao(title = step.title, text = step.text)
    }
}

@Serializable
data class IngredientDao(val name: String, val amount: AmountDao, val brand: String? = null) {
    fun asIngredient() = Ingredient(name = name, amount = amount.asAmount(), brand = brand)

    companion object {
        fun from(ingredient: Ingredient) = IngredientDao(
            name = ingredient.name,
            amount = AmountDao.from(ingredient.amount),
            brand = ingredient.brand
        )
    }
}

@Serializable
data class AmountDao(val value: Double, val unit: String) {
    fun asAmount() = Amount(value = BigDecimal(value), units = MeasureUnit.from(unit) ?: MeasureUnit.NUMBER)

    companion object {
        fun from(amount: Amount) = AmountDao(value = amount.value.toDouble(), unit = amount.units())
    }
}