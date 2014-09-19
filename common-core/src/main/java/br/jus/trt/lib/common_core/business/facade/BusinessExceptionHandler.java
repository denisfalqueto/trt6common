package br.jus.trt.lib.common_core.business.facade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Esta anotação produz um efeito de tratamento genérico de exceções. Deve ser colocada sobre classes
 * da camada de negócio (Facade). Seu objetivo é prover um ponto único para tratamentos específicos de
 * exceções, como a transformação de exceções desconhecidas em TRTException.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@InterceptorBinding
public @interface BusinessExceptionHandler {

}
