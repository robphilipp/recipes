package com.digitalcipher.rest.mediation

import com.digitalcipher.services.domain.*
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.Instant

@Serializable
data class RecipeMo(
    val name: String,
    val createdOn: Long,
    val modifiedOn: Long? = null,
    val ingredients: List<IngredientMo>,
    val steps: List<StepMo>
) {
    fun asRecipe() = Recipe(
        name = name,
        createdTimestamp = Timestamp.from(Instant.ofEpochMilli(createdOn)),
        modifiedTimestamp = if (modifiedOn != null) Timestamp.from(Instant.ofEpochMilli(modifiedOn)) else null,
        ingredients = ingredients.map { it.asIngredient() },
        steps = steps.map { it.asStep() }
    )

    companion object {
        fun from(recipe: Recipe) = RecipeMo(
            name = recipe.name,
            createdOn = recipe.createdTimestamp?.toInstant()?.toEpochMilli() ?: 0,
            modifiedOn = recipe.modifiedTimestamp?.toInstant()?.toEpochMilli(),
            ingredients = recipe.ingredients.map { IngredientMo.from(it) },
            steps = recipe.steps.map { StepMo.from(it) }
        )
    }
}

@Serializable
data class StepMo(val title: String? = null, val text: String) {
    fun asStep() = Step(title = title, text = text)

    companion object {
        fun from(step: Step) = StepMo(title = step.title, text = step.text)
    }
}

@Serializable
data class IngredientMo(val name: String, val amount: AmountMo, val brand: String? = null) {
    fun asIngredient() = Ingredient(name = name, amount = amount.asAmount(), brand = brand)

    companion object {
        fun from(ingredient: Ingredient) = IngredientMo(
            name = ingredient.name,
            amount = AmountMo.from(ingredient.amount),
            brand = ingredient.brand
        )
    }
}

@Serializable
data class AmountMo(val value: Double, val unit: String) {
    fun asAmount() = Amount(value = BigDecimal(value), units = MeasureUnit.from(unit) ?: MeasureUnit.NUMBER)

    companion object {
        fun from(amount: Amount) = AmountMo(value = amount.value.toDouble(), unit = amount.units())
    }
}