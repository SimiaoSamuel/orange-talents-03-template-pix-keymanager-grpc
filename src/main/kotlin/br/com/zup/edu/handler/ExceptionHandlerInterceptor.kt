package br.com.zup.edu.handler

import io.grpc.stub.StreamObserver
import io.micronaut.aop.Around
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor(@Inject private val resolver: ExceptionHandlerResolver)
    : MethodInterceptor<Any,Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        return try{
            context.proceed()
        } catch (e: Exception){
            val observer = context
                .parameterValues
                .filterIsInstance<StreamObserver<*>>()
                .firstOrNull() as StreamObserver<*>

            val handler = resolver.resolve(e)
            val status = handler.handle(e)
            observer.onError(status.asRuntimeException())
            null
        }
    }
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Around
annotation class ErrorHandler()