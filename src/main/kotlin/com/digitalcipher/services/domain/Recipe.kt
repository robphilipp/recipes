package com.digitalcipher.services.domain

import java.math.BigDecimal

data class Recipe(val name: String, val ingredients: List<Ingredient>, val steps: List<Step>) {
}

data class Ingredient(val name: String, val amount: Amount, val brand: String?)

data class Step(val title: String?, val text: String)

/*
 | AMOUNTS
 */
open class Amount(val value: BigDecimal, val units: MeasureUnit) {
    fun units(): String = if (value <= BigDecimal.ONE) units.literal else units.plural
}

data class Milligrams(private val amount: BigDecimal): Amount(amount, MeasureUnit.MILLIGRAMS)
data class Grams(private val amount: BigDecimal): Amount(amount, MeasureUnit.GRAMS)
data class Kilograms(private val amount: BigDecimal): Amount(amount, MeasureUnit.KILOGRAMS)
data class Ounces(private val amount: BigDecimal): Amount(amount, MeasureUnit.OUNCE)
data class Pounds(private val amount: BigDecimal): Amount(amount, MeasureUnit.POUND)

data class Milliliters(private val amount: BigDecimal): Amount(amount, MeasureUnit.MILLILITER)
data class Liters(private val amount: BigDecimal): Amount(amount, MeasureUnit.LITER)
data class Teaspoons(private val amount: BigDecimal): Amount(amount, MeasureUnit.TEASPOON)
data class Tablespoons(private val amount: BigDecimal): Amount(amount, MeasureUnit.TABLESPOON)
data class FluidOunces(private val amount: BigDecimal): Amount(amount, MeasureUnit.FLUID_OUNCE)
data class Cup(private val amount: BigDecimal): Amount(amount, MeasureUnit.CUP)
data class Pint(private val amount: BigDecimal): Amount(amount, MeasureUnit.PINT)
data class Quart(private val amount: BigDecimal): Amount(amount, MeasureUnit.QUART)
data class Gallon(private val amount: BigDecimal): Amount(amount, MeasureUnit.GALLON)

data class Pieces(val amount: BigDecimal): Amount(amount, MeasureUnit.NUMBER)

enum class MeasureType {
    VOLUME, MASS, WEIGHT, PIECE
}

enum class MeasureUnit(val literal: String, val plural: String, val measure: MeasureType) {
    // mass
    MILLIGRAMS("mg", "mg", MeasureType.MASS),
    GRAMS("g", "g", MeasureType.MASS),
    KILOGRAMS("kg", "kg", MeasureType.MASS),

    // weight
    OUNCE("oz", "ozs", MeasureType.WEIGHT),
    POUND("lb", "lbs", MeasureType.WEIGHT),

    // volume
    MILLILITER("ml", "ml", MeasureType.VOLUME),
    LITER("l", "l", MeasureType.VOLUME),

    TEASPOON("tsp", "tsps", MeasureType.VOLUME),
    TABLESPOON("tbsp", "tbsps", MeasureType.VOLUME),

    FLUID_OUNCE("fl oz", "fl ozs", MeasureType.VOLUME),
    CUP("cup", "cups", MeasureType.VOLUME),
    PINT("pt", "pts", MeasureType.VOLUME),
    QUART("qt", "qts", MeasureType.VOLUME),
    GALLON("gal", "gals", MeasureType.VOLUME),

    NUMBER("", "", MeasureType.PIECE)
    ;

    companion object {
        fun from(literal: String): MeasureUnit? = values().firstOrNull {
            it.literal == literal || it.plural == literal
        }

        fun with(measure: MeasureType): List<MeasureUnit> = values().filter {
            it.measure == measure
        }

        fun masses(): List<MeasureUnit> = values().filter { it.measure == MeasureType.MASS }
        fun weights(): List<MeasureUnit> = values().filter { it.measure == MeasureType.WEIGHT }
        fun volumes(): List<MeasureUnit> = values().filter { it.measure == MeasureType.VOLUME }
        fun pieces(): List<MeasureUnit> = values().filter { it.measure == MeasureType.PIECE }
    }
}
