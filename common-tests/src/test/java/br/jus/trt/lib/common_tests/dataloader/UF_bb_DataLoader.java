package br.jus.trt.lib.common_tests.dataloader;

import br.jus.trt.lib.common_tests.domain.UF;

/**
 * Bean DataLoader para execução de testes unitários. Insere um
 * objeto {@link UF} com sigla "bb".
 * @author augusto
 *
 */
public class UF_bb_DataLoader extends HibernateDataLoader {

	@Override
	public void load() throws Exception {
		UF uf = new UF("bb");
		getEntityManager().persist(uf);
		getEntityManager().flush();
	}

}
