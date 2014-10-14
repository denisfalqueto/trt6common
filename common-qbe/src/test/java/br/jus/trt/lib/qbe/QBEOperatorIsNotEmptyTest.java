package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;

import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.operator.IsNotEmpty;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.domain.Cidade;
import br.jus.trt.lib.qbe.domain.Projeto;
import br.jus.trt.lib.qbe.domain.UF;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

/**
 * Testes do operador {@link IsNotEmpty} 
 */
public class QBEOperatorIsNotEmptyTest extends QbeTestBase {

	@Test
	public void searchInSimpleAssociation() throws Exception {
		createCidadeUFScenario();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());;

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.isNotEmpty());
		filtro.paginate(0, 3); // para evitar um número muito grande de registros
		
		List<UF> ufs = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", ufs.isEmpty());
		
		// varre a lista verificando se todas as UFs encontradas não possuem cidades
		for (UF uf : ufs) {
			assertFalse("A UF deveria ter cidades associadas: " + uf, uf.getCidades().isEmpty());
		}		
		
	}
	
	/**
	 * Utiliza o operador IsNotEmpty em uma associação com tabela associativa mapeada
	 * com @JoinTable
	 */
	@Test
	public void searchInAssociationTable() {
		// utiliza os dados pré-fabricados no arquivo script_dml.sql

		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<Projeto> filtro = new QBEFilter<Projeto>(Projeto.class);
		filtro.filterBy("ferramentas", Operators.isNotEmpty());
		filtro.paginate(0, 3); // para evitar um número muito grande de registros
		
		List<Projeto> projetos = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", projetos.isEmpty());
		
		// varre a lista verificando se todas os Projetos encontrados possuem ferramentas associadas
		for (Projeto projeto : projetos) {
			assertFalse("O projeto deveria ter ferramentas associadas: " + projeto, projeto.getFerramentas().isEmpty());
		}		
		
		
	}

	private void createCidadeUFScenario() {
		// cria duas novas UF para testes
		UF uf1 = new UF("T1");
		UF uf2 = new UF("T2");
		getJpa().save(uf1);
		getJpa().save(uf2);
		
		// cadastra todas as cidades na uf1
		Cidade cid1 = new Cidade("cidade1", uf1);
		Cidade cid2 = new Cidade("cidade2", uf1);
		Cidade cid3 = new Cidade("cidade3", uf1);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
	}
}
