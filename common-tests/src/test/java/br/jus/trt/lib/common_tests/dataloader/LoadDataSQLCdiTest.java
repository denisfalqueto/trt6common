package br.jus.trt.lib.common_tests.dataloader;

import javax.inject.Inject;

import org.junit.Test;

import br.jus.trt.lib.common_tests.LocalTransactionTestBase;

/**
 * Testa o uso da anotação {@link LoadData} em classes de teste para carregamento de dados com Beans.
 * @author augusto
 *
 */
@LoadData(sql="dataloader/uf_aa.sql")
public class LoadDataSQLCdiTest extends LocalTransactionTestBase implements LoadDataSQLTestDef {

	@Inject
	private LoadDataSQLTestImpl tester;
	
	@Override
	@Test
	public void loadSQLDataOnTypeTest() {
		tester.loadSQLDataOnTypeTest();
	}
	
	@Override
	@LoadData(sql="dataloader/uf_bb.sql")
	@Test
	public void loadSQLDataOnMethodTest() {
		tester.loadSQLDataOnMethodTest();
	}
	
}
