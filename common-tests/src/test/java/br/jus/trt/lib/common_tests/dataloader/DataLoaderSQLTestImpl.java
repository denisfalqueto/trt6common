package br.jus.trt.lib.common_tests.dataloader;

import javax.inject.Inject;

import org.junit.Assert;

import br.jus.trt.lib.common_tests.util.QuerierUtil;

/**
 * Testes da classe {@link SqlDataLoader}
 * 
 * @author augusto
 */
public class DataLoaderSQLTestImpl implements DataLoaderSQLTestDef {

	@Inject
	private SqlDataLoader loaderSQL;

	@Inject
	private QuerierUtil querierUtil;
	
	@Override
	public void loadScriptTest() throws Exception {
		// garante que não há nenhuma uf com sigla "aa"
		long count = querierUtil.executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(0, count);
		
		loaderSQL.setScriptPath("dataloader/uf_aa.sql");
		loaderSQL.load();
		
		// buscando o registro para confirmação
		count = querierUtil.executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);
	}

}
