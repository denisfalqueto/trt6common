package br.jus.trt.lib.qbe;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.domain.Cidade;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

public class OrderTest extends QbeTestBase {

	@Test
	public void consultarEntidadeComOrdenacaoAscendenteSimples() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "from " + Cidade.class.getSimpleName() + " order by nome asc";
		@SuppressWarnings("unchecked")
		List<Cidade> cidadesHQL = getQuerier().executeQuery(getEntityManager(), hql, 0, 50, (Object[]) null);
		
		// deve haver pelo menos uma cidade cadastrada
		if (cidadesHQL == null || cidadesHQL.isEmpty()) {
			fail("Não há cidadess cadastradas, não é possível proceder com o teste");
		}		
		
		// executa a consulta utilizando QBE
		QBEFilter<Cidade> consulta = new QBEFilter<Cidade>(Cidade.class);
		consulta.sortAscBy("nome");
		consulta.paginate(0, 50);
		
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Cidade> cidadesQbe = qbe.search(consulta);
		
		// compara as listas
		assertTrue("O resultado da consulta QBE n�o confere com a consulta HQL equivalente", cidadesHQL.containsAll(cidadesQbe));
		assertTrue("O resultado da consulta QBE n�o confere com a consulta HQL equivalente", cidadesQbe.containsAll(cidadesHQL));
		
		for (int i = 0; i < cidadesHQL.size(); i++) {
			assertEquals("As cidades deveriam ser iguais em todas as posi��es. Posi��o de falha: " + i, cidadesHQL.get(i), cidadesQbe.get(i));
		}
		
	}
	
	@Test
	public void consultarEntidadeComOrdenacaoDescendenteSimples() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "from " + Cidade.class.getSimpleName() + " order by nome desc";
		@SuppressWarnings("unchecked")
		List<Cidade> cidadesHQL = getQuerier().executeQuery(getEntityManager(), hql, 0, 50, (Object[]) null);
		
		// deve haver pelo menos uma cidade cadastrada
		if (cidadesHQL == null || cidadesHQL.isEmpty()) {
			fail("N�o h� cidadess cadastradas, n�o � poss�vel proceder com o teste");
		}		
		
		// executa a consulta utilizando QBE
		QBEFilter<Cidade> consulta = new QBEFilter<Cidade>(Cidade.class);
		consulta.sortDescBy("nome");
		consulta.paginate(0, 50);
		
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Cidade> cidadesQbe = qbe.search(consulta);
		
		// compara as listas
		assertTrue("O resultado da consulta QBE n�o confere com a consulta HQL equivalente", cidadesHQL.containsAll(cidadesQbe));
		assertTrue("O resultado da consulta QBE n�o confere com a consulta HQL equivalente", cidadesQbe.containsAll(cidadesHQL));
		
		for (int i = 0; i < cidadesHQL.size(); i++) {
			assertEquals("As cidades deveriam ser iguais em todas as posi��es. Posi��o de falha: " + i, cidadesHQL.get(i), cidadesQbe.get(i));
		}
		
	}
	
	@Test
	public void consultarEntidadeComOrdenacaoPropriedadeAninhada() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "from " + Cidade.class.getSimpleName() + " order by uf.sigla asc, nome asc";
		@SuppressWarnings("unchecked")
		List<Cidade> cidadesHQL = getQuerier().executeQuery(getEntityManager(), hql, 0, 50, (Object[]) null);
		
		// deve haver pelo menos uma cidade cadastrada
		if (cidadesHQL == null || cidadesHQL.isEmpty()) {
			fail("Não há cidadess cadastradas, não é possível proceder com o teste");
		}		
		
		// executa a consulta utilizando QBE
		QBEFilter<Cidade> consulta = new QBEFilter<Cidade>(Cidade.class);
		consulta.sortAscBy("uf.sigla", "nome");
		consulta.paginate(0, 50);
		
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Cidade> cidadesQbe = qbe.search(consulta);
		
		// compara as listas
		assertTrue("O resultado da consulta QBE não confere com a consulta HQL equivalente", cidadesHQL.containsAll(cidadesQbe));
		assertTrue("O resultado da consulta HQL não confere com a consulta QBE equivalente", cidadesQbe.containsAll(cidadesHQL));
		
		for (int i = 0; i < cidadesHQL.size(); i++) {
			assertEquals("As cidades deveriam ser iguais em todas as posições. Posição de falha: " + i, cidadesHQL.get(i), cidadesQbe.get(i));
		}
		
	}	
	
}
