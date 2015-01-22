package br.jus.trt.lib.common_tests.dataloader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Esta anotação permite a configuração de múltiplos DataLoaders para serem executados em um determinado
 * teste automático.
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@InterceptorBinding
public @interface LoadDatas {

	/**
	 * @return Conjunto de {@link LoadData} a ser executado.
	 */
	@Nonbinding
	LoadData[] value() default {};
	
}

