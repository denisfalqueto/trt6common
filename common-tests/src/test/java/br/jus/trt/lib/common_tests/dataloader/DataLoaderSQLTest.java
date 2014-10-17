package br.jus.trt.lib.common_tests.dataloader;

import javax.inject.Inject;

import org.junit.Test;

import br.jus.trt.lib.common_tests.jpa.TransactionTestBase;


/**
 * Testes da classe {@link DataLoaderSQL}
 * @author augusto
 */
public class DataLoaderSQLTest extends TransactionTestBase {

	@Inject
	private DataLoaderSQL loaderSQL;
	
	/**
	 * Tenta executar um script existente na raiz do projeto, considerando seu caminho relativo.
	 */
	@Test
	public void loadScriptRaizTest() {
		loaderSQL.setScriptPath("uf_aa.sql");
	}
	
}
