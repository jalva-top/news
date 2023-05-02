package com.example.news.api.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [KeywordValidator::class])
@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY_GETTER
)
@MustBeDocumented
annotation class ValidKeywords(
    val message: String = "{keyword.value.valid}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class KeywordValidator : ConstraintValidator<ValidKeywords, List<String>?> {
    companion object {
        const val MAX = 50
    }

    override fun isValid(keywords: List<String>?, ctx: ConstraintValidatorContext): Boolean =
        keywords == null || keywords.all { it.isNotBlank() && it.length < MAX }
}
