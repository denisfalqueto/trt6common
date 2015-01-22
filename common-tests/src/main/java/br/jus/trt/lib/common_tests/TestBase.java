package br.jus.trt.lib.common_tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.cdi.CdiJUnitRunner;

/**
 * Classe base com comportamento comum para testes unitários simples (pojo).
 * Gerencia o ciclo de vida do JUnit, integrando a classe a um Container CDI (o
 * que significa que Injeção de Dependências e todos os seus recursos são
 * suportados desde a classe de testes).
 * 
 * @author Augusto
 * 
 */
@RunWith(CdiJUnitRunner.class)
@Ignore
public abstract class TestBase {

	/**
	 * Executado antes de cada método de teste.
	 */
	@Before
	public void beforeEachTest() {
	};

	/**
	 * Executado após de cada método de teste.
	 */
	@After
	public void afterEachTest() {
	};

	/**
	 * Verifica se ambas as listas possuem os mesmos objetos. A ordem não é
	 * verificada.
	 */
	public void assertContentEqual(List<?> list1, List<?> list2) {
		assertTrue(
				"A primeira lista não contem todos os objetos da segunda lista",
				list1.containsAll(list2));
		assertTrue(
				"A segunda lista não contem todos os objetos da primeira lista",
				list2.containsAll(list1));
	}

	/**
	 * Verifica se ambas as listas não possuem os mesmos objetos. A ordem não é
	 * verificada.
	 */
	public void assertContentNotEqual(List<?> list1, List<?> list2) {
		boolean hqlContemQBE = list1.containsAll(list2);
		boolean qbeContemHQL = list2.containsAll(list1);
		assertFalse("As listas deveriam conter dados equivalentes.",
				hqlContemQBE && qbeContemHQL);
	}

	/**
	 * Verifica se a coleção é diferente de null e não vazia.
	 * 
	 * @param collection
	 *            Para validação.
	 */
	protected void assertNotEmpty(Collection<?> collection) {
		assertTrue(collection != null && !collection.isEmpty());
	}

	/**
	 * Verifica se a coleção é diferente de null e vazia.
	 * 
	 * @param collection
	 *            Para validação.
	 */
	protected void assertEmpty(Collection<?> collection) {
		assertTrue(collection != null && collection.isEmpty());
	}

	/**
	 * Verifica se a coleção é null ou vazia.
	 * 
	 * @param collection
	 *            Para validação.
	 */
	protected void assertNullOrEmpty(Collection<?> collection) {
		assertTrue(collection == null || collection.isEmpty());
	}

}
