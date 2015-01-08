package br.jus.trt.lib.common_tests.dataloader;

import org.junit.Assert;
import org.junit.Test;

import br.jus.trt.lib.common_tests.LocalTransactionTestBase;
import br.jus.trt.lib.common_tests.domain.UF;

/**
 * Testa o uso da anotação {@link LoadData} em classes de teste para carregamento de scripts SQL.
 * @author augusto
 *
 */
@LoadData(dataLoader=UF_aa_DataLoader.class)
public class LoadDataBeanTest extends LocalTransactionTestBase {

	/**
	 * Verifica o funcionamento da anotação sobre a classe.
	 */
	@Test
	public void loadDataInTypeTest() {
		// garantindo que há apenas 1 registro na base de dados
		long count = executeCountQuery("select count(uf) from UF uf");
		Assert.assertEquals(1, count);
		
		// buscando o registro para confirmação
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);		
	}
	
	/**
	 * Verifica o funcionamento da anotação sobre um método.
	 * Neste caso, deve considerar o que há sobre a classe e sobre o método.
	 */
	@LoadData(dataLoader=UF_bb_DataLoader.class)
	@Test
	public void loadDataInMethodTest() {
		// garantindo que há apenas 2 registros na base de dados
		long count = executeCountQuery("select count(uf) from UF uf");
		Assert.assertEquals(2, count);
		
		// buscando o registro para confirmação
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);
		
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "bb");
		Assert.assertEquals(1, count);		
	}
	
	/**
	 * Teste de precendência de execução do {@link DataLoader}
	 */
	@LoadData(dataLoader=UF_bb_DataLoader.class, precedence=2)
	@Test
	public void precedenceTest() {
		
		/*
		 * Considerando que a precedência default é 1, e quando maior o valor maior a precendência,
		 * espera-se que seja executado primeiro o script associado ao método e depois o da classe.
		 * A verificação será realizada pela comparação dos ID's, visto que são provenientes de sequences.
		 */
		// buscando o registro para confirmação
		UF uf_aa = (UF) executeQuery("select uf from UF uf where uf.sigla=?", "aa").get(0);
		UF uf_bb = (UF) executeQuery("select uf from UF uf where uf.sigla=?", "bb").get(0);
		Assert.assertTrue(uf_aa.getId() > uf_bb.getId());		
		
	}
}
