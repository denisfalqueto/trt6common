package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;

import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.domain.Cidade;
import br.jus.trt.lib.qbe.domain.UF;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

public class RepositorioQBETest extends QbeTestBase {

	/**
	 * O ID da entidade, quando preenchido deve ser priorizado sobre os demais atributos,
	 * exceto quando esta for a entidade raiz da operação.
	 */
	@Test
	public void testNaoPriorizacaoID() {
		
		// cria uma cidade qualquer
		UF ufBD = getQuerier().findAny(UF.class);
		
		Cidade cid1 = new Cidade("cid1", ufBD);
		getJpa().save(cid1);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// em seguida tenta realizar uma consulta utilizando como restrição
		// o id da cidade criada e um nome diferente do cadastrado
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(new Cidade());
		filtro.getExample().setId(cid1.getId());
		filtro.getExample().setNome("outro nome");
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		long numCidades = qbe.count(filtro);
		
		/*
		 * visto que cidade é a entidade raiz da consulta,
		 * o id não deveria ter tipo prioridade sobre as demais propriedades.
		 * Se houve resultado na consulta, significa que o nome não foi considerado.
		 */
		assertEquals(0, numCidades);
	}

	/**
	 * O ID da entidade, quando preenchido deve ser priorizado sobre os demais atributos,
	 * apenas quando se tratar de um relacionamento da exemplo base do filtro.
	 */
	@Test
	public void testPriorizacaoID() {
		
		// cria uma cidade qualquer
		UF ufBD = getQuerier().findAny(UF.class);
		
		Cidade cid1 = new Cidade("cid1", ufBD);
		getJpa().save(cid1);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// vamos realiza duas consultas, a primeira deverá buscar todas as cidades associadas a uma UF com o mesmo ID da
		// utilizada acima. A segunda deverá consultar todas as cidades associadas a uma UF com o mesmo ID da utilizada acima,
		// mas um nome diferente. Pelo princípio da priorização da chave primária em associações, o resultado deverá ser o mesmo.
		
		
		QBEFilter<Cidade> filtro1 = new QBEFilter<Cidade>(new Cidade());
		filtro1.getExample().setUf(new UF());
		filtro1.getExample().getUf().setId(ufBD.getId());
	
		QBEFilter<Cidade> filtro2 = new QBEFilter<Cidade>(new Cidade());
		filtro2.getExample().setUf(new UF());
		filtro2.getExample().getUf().setId(ufBD.getId());
		filtro2.getExample().getUf().setSigla("ZZ");

		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Cidade> cidades1 = qbe.search(filtro1);
		List<Cidade> cidades2 = qbe.search(filtro2);
		
		assertContentEqual(cidades1, cidades2);
	}

	
	@Test
	public void consultarEntidadeListar() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "from " + UF.class.getSimpleName();
		@SuppressWarnings("unchecked")
		List<UF> ufsHQL = getQuerier().executeQuery(getEntityManager(), hql);
		
		// deve haver pelo menos uma UF cadastrada
		if (ufsHQL == null || ufsHQL.isEmpty()) {
			fail("Não há UFs cadastradas, não é possível proceder com o teste");
		}
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<UF> ufsQbe = qbe.search(new QBEFilter<UF>(UF.class));
		
		// compara as listas
		assertTrue("O resultado da consulta QBE não confere com a consulta HQL equivalente", ufsHQL.containsAll(ufsQbe));
		assertTrue("O resultado da consulta QBE não confere com a consulta HQL equivalente", ufsQbe.containsAll(ufsHQL));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void consultarEntidadeComPaginacao() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "from " + Cidade.class.getSimpleName();
		List<Cidade> cidadesHQL = getQuerier().executeQuery(getEntityManager(), hql, 1, 50, (Object[]) null);
		
		// deve haver pelo menos uma cidade cadastrada
		if (cidadesHQL == null || cidadesHQL.isEmpty()) {
			fail("Não há cidadess cadastradas, não é possível proceder com o teste");
		}
		
		// executa a consulta utilizando QBE
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(Cidade.class);
		filtro.paginate(1, 50);
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// compara as listas
		assertTrue("O resultado da consulta QBE não confere com a consulta HQL equivalente", cidadesHQL.containsAll(cidadesQbe));
		assertTrue("O resultado da consulta QBE não confere com a consulta HQL equivalente", cidadesQbe.containsAll(cidadesHQL));
		
		for (int i = 0; i < cidadesHQL.size(); i++) {
			assertEquals("As cidades deveriam ser iguais em todas as posiçes. Posição de falha: " + i, cidadesHQL.get(i), cidadesQbe.get(i));
		}		
	}
	
}
