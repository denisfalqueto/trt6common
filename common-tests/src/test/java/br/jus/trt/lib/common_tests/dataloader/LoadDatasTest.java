package br.jus.trt.lib.common_tests.dataloader;

import org.junit.Assert;
import org.junit.Test;

import br.jus.trt.lib.common_tests.domain.UF;
import br.jus.trt.lib.common_tests.jpa.TransactionTestBase;

/**
 * Testa o uso da anotação {@link LoadDatas}, misturando as opções de script e Bean.
 * @author augusto
 *
 */
@LoadDatas ({
	@LoadData(sql="dataloader/uf_aa.sql"),
	@LoadData(dataLoader=UF_bb_DataLoader.class, precedence=3)
})	
public class LoadDatasTest extends TransactionTestBase {

	@Test
	public void loadDatasInTypeTest() {
		// garantindo que há apenas 2 registros na base de dados
		long count = executeCountQuery("select count(uf) from UF uf");
		Assert.assertEquals(2, count);
		
		// buscando o registro para confirmação
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);
		
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "bb");
		Assert.assertEquals(1, count);		
	}

	@LoadDatas ({
		@LoadData(sql="dataloader/uf_cc.sql"),
		@LoadData(dataLoader=UF_dd_DataLoader.class)
	})
	@Test
	public void loadDatasInMethodTest() {
		// garantindo que há 4 registros na base de dados
		long count = executeCountQuery("select count(uf) from UF uf");
		Assert.assertEquals(4, count);
		
		// buscando o registro para confirmação
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);
		
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "bb");
		Assert.assertEquals(1, count);
		
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "cc");
		Assert.assertEquals(1, count);
		
		count = executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "dd");
		Assert.assertEquals(1, count);
	}

	
	/**
	 * Teste de precendência de execução do {@link DataLoader}
	 */
	@LoadDatas ({
		@LoadData(sql="dataloader/uf_cc.sql", precedence=2),
		@LoadData(dataLoader=UF_dd_DataLoader.class, precedence=4)
	})
	@Test
	public void precedenceTest() {
		/*
		 * foi feito um jogo de precendências na declaração dos loaders na classe e no método.
		 * Lembrando que precedência maior é executado primeiro, portanto tem menor ID.
		 */
		// buscando o registro para confirmação
		UF uf_aa = (UF) executeQuery("select uf from UF uf where uf.sigla=?", "aa").get(0); //p=1
		UF uf_cc = (UF) executeQuery("select uf from UF uf where uf.sigla=?", "cc").get(0); //p=2 
		UF uf_bb = (UF) executeQuery("select uf from UF uf where uf.sigla=?", "bb").get(0); //p=3
		UF uf_dd = (UF) executeQuery("select uf from UF uf where uf.sigla=?", "dd").get(0); //p=4 
		
		Assert.assertTrue(uf_aa.getId() > uf_bb.getId());
		Assert.assertTrue(uf_aa.getId() > uf_cc.getId());
		Assert.assertTrue(uf_aa.getId() > uf_dd.getId());
		
		Assert.assertTrue(uf_cc.getId() > uf_bb.getId());
		Assert.assertTrue(uf_cc.getId() > uf_dd.getId());

		Assert.assertTrue(uf_bb.getId() > uf_dd.getId());
	}
	
}
