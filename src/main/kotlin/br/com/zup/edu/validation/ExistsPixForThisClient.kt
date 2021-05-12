package br.com.zup.edu.validation

import br.com.zup.edu.repository.PixRepository
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PixValidator::class])
annotation class ExistsPixForThisClient(
    val message: String = "Esse cliente já cadastrou sua chave pix",
    val payload: Array<KClass<Payload>> = [],
    val groups: Array<KClass<Any>> = []
)

@Singleton
class PixValidator: ConstraintValidator<ExistsPixForThisClient, String> {

    @Inject
    lateinit var repository: PixRepository

    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<ExistsPixForThisClient>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value == null)
            return false

        return !repository.existsByOwner(value)
    }
}