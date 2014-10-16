package br.jus.trt.lib.common_tests.dataloader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Esta anotação permite a configuração de DataLoaders para serem executados antes de classes e métodos de teste.
 * Quando colocado sobre uma classe, o DataLoader será executado apenas uma vez antes da execução dos métodos
 * de testes da classe. Quando colocado antes de um método de teste, será executado uma vez antes deste método 
 * específico. 
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@InterceptorBinding
public @interface LoadData {

	/**
	 * @return Caminhos relativos para os scripts a serem executados. 
	 * Serão executados na sequência informada.
	 */
	@Nonbinding
	String[] sql() default {};
	
	/**
	 * @return {@link DataLoader} que realizará a carga de dados.
	 */
	@Nonbinding
	Class<? extends DataLoader>[] dataLoader() default {};
	
	/**
	 * @return A precedência de execução dos DataLoader, permitindo a configuração de
	 * mais de um {@link DataLoader} em um mesmo Bean.
	 * @see LoadDatas
	 */
	@Nonbinding
	int precedence() default 0;
}
