package br.jus.trt.lib.common_tests.dataloader.cdi;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import br.jus.trt.lib.common_tests.LocalTransactionTestBase;
import br.jus.trt.lib.common_tests.dataloader.HibernateDataLoader;
import br.jus.trt.lib.common_tests.dataloader.UF_aa_DataLoader;

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
		long count = getQuerier().executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(0, count);
		
		ufDataLoader.load();
		
		// buscando o registro para confirmação
		count = getQuerier().executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);		
	}
}
