package br.jus.trt.lib.common_core.business.facade;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.cdi.CdiJUnitRunner;

/**
 * Classe base com comportamento comum para testes unitários que utilizam um
 * contexto transacional. Gerencia o ciclo de vida do JUnit, JPA e dos recursos
 * necessários para testes.
 * 
 * @author Augusto
 * 
 */
@RunWith(CdiJUnitRunner.class)
public class CDITest {

	@Test
	public void repositoryTypeInjectionTest() {
	}

}
