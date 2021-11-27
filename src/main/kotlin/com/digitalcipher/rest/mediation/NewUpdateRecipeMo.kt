package com.digitalcipher.rest.mediation

import com.digitalcipher.services.domain.Recipe
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.time.Clock
import java.time.Instant

@Serializable
data class NewUpdateRecipeMo(
    val name: String,
    val ingredients: List<IngredientMo>,
    val steps: List<StepMo>
) {
    fun asRecipe() = Recipe(
        name = name,
        createdTimestamp = Timestamp.from(Instant.now(Clock.systemUTC())),
        modifiedTimestamp = null,
        ingredients = ingredients.map { it.asIngredient() },
        steps = steps.map { it.asStep() }
    )
}
