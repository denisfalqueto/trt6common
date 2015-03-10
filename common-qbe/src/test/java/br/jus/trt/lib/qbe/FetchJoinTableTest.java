package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Hibernate;
import org.junit.Test;

import br.jus.trt.lib.qbe.api.FetchMode;
import br.jus.trt.lib.qbe.api.JoinType;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.domain.Projeto;
import br.jus.trt.lib.qbe.domain.ProjetoServidor;
import br.jus.trt.lib.qbe.domain.QProjeto;
import br.jus.trt.lib.qbe.domain.QProjetoServidor;
import br.jus.trt.lib.qbe.repository.criteria.CollectionJoinTableFetcher;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

/**
 * Casos de teste para mapeamentos de coleção utilizado @JoinTable.
 * Principal classe testada: {@link CollectionJoinTableFetcher}
 * @author augusto
 *
 */
public class FetchJoinTableTest extends QbeTestBase {

	@Test
	public void consultarEntidadeComFetchInnerJoin() {

		/*
		 * O script de carga contém projetos com e sem associação com ferramntas.
		 */
		
		// realiza mesma consulta com QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Projeto> filtro = new QBEFilter<Projeto>(Projeto.class);
		filtro.addFetch(new FetchMode(QProjeto.projeto.ferramentas(), JoinType.INNER)); 
		
		List<Projeto> projetosQbe = qbe.search(filtro);

		assertNotEmpty(projetosQbe);
		
		for (Projeto projeto : projetosQbe) {
			assertTrue("As ferramentas deveriam estar inicializadas: " + projeto, Hibernate.isInitialized(projeto.getFerramentas()));
			assertFalse("O projeto deveria ter ferramentas associadas: " + projeto, projeto.getFerramentas().isEmpty());
		}
		
	}
	
	/**
	 * Testa o fetch de coleções mapeadas com tabela associativa através de @JoinTable
	 */
	@Test
	public void fetchColecao() {
		
		// utiliza os dados pré-fabricados no arquivo script_dml.sql

		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<Projeto> filtro = new QBEFilter<Projeto>(Projeto.class);
		filtro.filterBy(QProjeto.projeto.ferramentas(), Operators.isNotEmpty());
		filtro.addFetch(QProjeto.projeto.ferramentas());
		filtro.paginate(0, 3); // para evitar um número muito grande de registros
		
		List<Projeto> projetos = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", projetos.isEmpty());
		
		// varre a lista verificando se todas os Projetos encontrados possuem ferramentas associadas
		for (Projeto projeto : projetos) {
			assertTrue("As ferramentas deveriam estar inicializadas: " + projeto, Hibernate.isInitialized(projeto.getFerramentas()));
			assertFalse("O projeto deveria ter ferramentas associadas: " + projeto, projeto.getFerramentas().isEmpty());
		}
		
	}
	
	/**
	 * Testa o fetch de coleções mapeadas com tabela associativa através de @JoinTable, em um
	 * cenário com aninhamento
	 */
	@Test
	public void fetchColecaoAninhada() {
		
		// utiliza os dados pré-fabricados no arquivo script_dml.sql

		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<ProjetoServidor> filtro = new QBEFilter<ProjetoServidor>(ProjetoServidor.class);
		filtro.filterBy(QProjetoServidor.projetoServidor.projeto().ferramentas(), Operators.isNotEmpty());
		filtro.addFetch(QProjetoServidor.projetoServidor.projeto().ferramentas());
		filtro.paginate(0, 3); // para evitar um número muito grande de registros
		
		List<ProjetoServidor> projetos = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", projetos.isEmpty());
		
		// varre a lista verificando se todas os Projetos encontrados possuem ferramentas associadas
		for (ProjetoServidor projetoServidor : projetos) {
			assertTrue("O projeto deveria estar inicializado: " + projetoServidor, Hibernate.isInitialized(projetoServidor.getProjeto()));
			assertTrue("As ferramentas deveriam estar inicializadas: " + projetoServidor, Hibernate.isInitialized(projetoServidor.getProjeto().getFerramentas()));
			assertFalse("O projeto deveria ter ferramentas associadas: " + projetoServidor, projetoServidor.getProjeto().getFerramentas().isEmpty());
		}
		
	}
}
