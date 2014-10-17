package br.jus.trt.lib.common_tests.dataloader;

import br.jus.trt.lib.common_tests.domain.UF;

/**
 * Bean DataLoader para execução de testes unitários. Insere um
 * objeto {@link UF} com sigla "dd".
 * @author augusto
 *
 */
public class UF_dd_DataLoader extends HibernateDataLoader {

	@Override
	public void load() throws Exception {
		UF uf = new UF("dd");
		getEntityManager().persist(uf);
		getEntityManager().flush();
	}

}