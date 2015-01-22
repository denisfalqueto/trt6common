package br.jus.trt.lib.common_tests.dataloader;

import javax.inject.Inject;

import org.junit.Assert;

import br.jus.trt.lib.common_tests.domain.UF;
import br.jus.trt.lib.common_tests.util.QuerierUtil;

/**
 * Testa o uso da anotação {@link LoadData} em classes de teste para carregamento de scripts SQL.
 * @author augusto
 *
 */
public class LoadDataBeanTestImpl implements LoadDataBeanTestDef {

	@Inject
	private QuerierUtil querierUtil;
	
	/* (non-Javadoc)
	 * @see br.jus.trt.lib.common_tests.dataloader.cdi.LoadDataBeanTestDef#loadDataInTypeTest()
	 */
	@Override
	public void loadDataInTypeTest() {
		// garantindo que há apenas 1 registro na base de dados
		long count = querierUtil.executeCountQuery("select count(uf) from UF uf");
		Assert.assertEquals(1, count);
		
		// buscando o registro para confirmação
		count = querierUtil.executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);		
	}
	
	/* (non-Javadoc)
	 * @see br.jus.trt.lib.common_tests.dataloader.cdi.LoadDataBeanTestDef#loadDataInMethodTest()
	 */
	@Override
	public void loadDataInMethodTest() {
		// garantindo que há apenas 2 registros na base de dados
		long count = querierUtil.executeCountQuery("select count(uf) from UF uf");
		Assert.assertEquals(2, count);
		
		// buscando o registro para confirmação
		count = querierUtil.executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);
		
		count = querierUtil.executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "bb");
		Assert.assertEquals(1, count);		
	}
	
	/* (non-Javadoc)
	 * @see br.jus.trt.lib.common_tests.dataloader.cdi.LoadDataBeanTestDef#precedenceTest()
	 */
	@Override
	public void precedenceTest() {
		
		/*
		 * Considerando que a precedência default é 1, e quando maior o valor maior a precendência,
		 * espera-se que seja executado primeiro o script associado ao método e depois o da classe.
		 * A verificação será realizada pela comparação dos ID's, visto que são provenientes de sequences.
		 */
		// buscando o registro para confirmação
		UF uf_aa = (UF) querierUtil.executeQuery("select uf from UF uf where uf.sigla=?", "aa").get(0);
		UF uf_bb = (UF) querierUtil.executeQuery("select uf from UF uf where uf.sigla=?", "bb").get(0);
		Assert.assertTrue(uf_aa.getId() > uf_bb.getId());		
		
	}
}
