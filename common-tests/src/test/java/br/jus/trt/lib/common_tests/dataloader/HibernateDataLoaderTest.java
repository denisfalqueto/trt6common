package br.jus.trt.lib.common_tests.dataloader;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import br.jus.trt.lib.common_tests.LocalTransactionTestBase;

/**
 * Teste para a classe {@link HibernateDataLoader}
 * @author augusto
 *
 */
public class HibernateDataLoaderTest extends LocalTransactionTestBase {

	@Inject
	private UF_aa_DataLoader ufDataLoader;
	
	@Test
	public void simpleDataLoaderTest() throws Exception {
		// garante que não há nenhuma uf com sigla "aa"
		long count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(0, count);
		
		ufDataLoader.load();
		
		// buscando o registro para confirmação
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);		
	}
}
