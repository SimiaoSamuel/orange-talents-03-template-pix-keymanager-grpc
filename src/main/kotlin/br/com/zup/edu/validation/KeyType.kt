package br.com.zup.edu.validation

import br.com.zup.edu.dto.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationTarget.*
import kotlin.annotation.AnnotationRetention.*
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [KeyValidator::class])
annotation class ValidKey(
    val message: String = "chave inválida para o tipo escolhido",
    val payload: Array<KClass<Payload>> = [],
    val groups: Array<KClass<Any>> = []
)

@Singleton
class KeyValidator: ConstraintValidator<ValidKey,NovaChavePix>{
    override fun isValid(
        value: NovaChavePix?,
        annotationMetadata: AnnotationValue<ValidKey>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value?.tipoChave == null)
            return false

       return value.tipoChave!!.validate(value.chave)
    }
}