package br.jus.trt.lib.common_tests.cdi;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.util.EmptyBeanA;

@RunWith(CdiJUnitRunner.class)
public class CdiJUnitRunnerTest {

	@Inject
	private EmptyBeanA emptyBeanA;
	
	/**
	 * Verifica se a injeção de dependência está sendo habilitada para a classe de teste
	 * que executa com {@link CdiJUnitRunner}
	 */
	@Test
	public void cdiBeanInjectionTest() {
		Assert.assertNotNull(emptyBeanA);
	}
	
	/**
	 * Verifica se a injeção de pripriedades aninhadas está funcionando para a classe de teste
	 * que executa com {@link CdiJUnitRunner}
	 */
	@Test
	public void cdiBeanNestedInjectionTest() {
		Assert.assertNotNull(emptyBeanA.getEmptyBeanB());
	}
	
}
