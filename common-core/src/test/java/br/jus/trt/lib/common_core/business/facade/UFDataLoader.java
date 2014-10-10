package br.jus.trt.lib.common_core.business.facade;

import br.jus.trt.lib.common_core.domain.UF;
import br.jus.trt.lib.common_tests.dataloader.HibernateDataLoader;

public class UFDataLoader extends HibernateDataLoader {

	@Override
	public void load() throws Exception {
		getLogger().info("Executando carga de dados... ");
		
		UF uf = new UF();
		uf.setId(200L);
		uf.setSigla("XX");
		
		getEntityManager().merge(uf);
		getEntityManager().flush();
	}

}
