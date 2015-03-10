package br.jus.trt.lib.common_tests.dataloader;

import javax.inject.Inject;

import org.junit.Test;

import br.jus.trt.lib.common_tests.LocalTransactionTestBase;

/**
 * Teste para a classe {@link HibernateDataLoader}
 * @author augusto
 *
 */
public class HibernateDataLoaderCdiTest extends LocalTransactionTestBase implements HibernateDataLoaderTestDef {

	@Inject
	private HibernateDataLoaderTestImpl tester;
	
	@Override
	@Test
	public void simpleDataLoaderTest() throws Exception {
		tester.simpleDataLoaderTest();		
	}
}
