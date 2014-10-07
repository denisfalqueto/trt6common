package br.jus.trt.lib.common_tests.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.cdi.CdiJUnitRunner;

/**
 * Classe base com comportamento comum para testes unitários que utilizam um
 * contexto transacional. Gerencia o ciclo de vida do JUnit, JPA e dos recursos
 * necessários para testes.
 * 
 * @author Augusto
 * 
 */
@RunWith(CdiJUnitRunner.class)
@Ignore
public class BaseTest {

	@Inject
	private JPAStandalone jpaInstance;

	@BeforeClass
	public static void beforeAll() {
	}

	@Before
	public void beforeEach() {
		jpaInstance.startSession();
		jpaInstance.startTransaction();
	}

	@After
	public void afterEach() {
		jpaInstance.rollbackTransaction();
		jpaInstance.closeSession();
	}

	public EntityManager getEntityManager() {
		return jpaInstance.getEm();
	}

	@Produces @Default
	public JPAStandalone getJpa() {
		return jpaInstance;
	}

	public void setJpa(JPAStandalone jpa) {
		this.jpaInstance = jpa;
	}

	/**
	 * Busca objetos de exemplo, validando se o resultado cont�m pelo menos um
	 * registro.
	 * 
	 * @param hql
	 *            HQL para buscar os objetos.
	 * @param valores
	 *            Valores de filtro no hql
	 * @return Lista de entidades de acordo com o hql informado.
	 */
	protected <TIPO> List<TIPO> searchAndValidate(String hql, Object... valores) {
		@SuppressWarnings("unchecked")
		List<TIPO> resultadoHQL = executeQuery(getJpa().getEm(), hql, valores);

		// deve haver pelo menos uma Cidade cadastrada
		if (resultadoHQL == null || resultadoHQL.isEmpty()) {
			fail("A consulta n�o retornou resultado, por isso n�o � poss�vel proceder com os testes");
		}

		return resultadoHQL;
	}

	/**
	 * Executa um hql com o entitymanager corrente.
	 * 
	 * @param em
	 *            EntityManager para realização da operação.
	 * @param hql
	 *            HQL a ser executado.
	 * @param parametros
	 *            Parâmetros a serem setados na query. Opcional.
	 * @return Lista de retorno da query.
	 */
	protected Long executeCountQuery(EntityManager em, String hql,
			Object... parametros) {
		Query query = createQuery(em, hql, parametros);
		return (Long) query.getSingleResult();
	}

	/**
	 * Cria uma query.
	 * 
	 * @param em
	 *            EntityManager para realização da operação.
	 * @param hql
	 *            HQL para criação da query.
	 * @param parametros
	 *            Parâmetros a serem adicionados à query.
	 * @return Query criada.
	 */
	protected Query createQuery(EntityManager em, String hql,
			Object... parametros) {
		Query query = em.createQuery(hql);

		if (parametros != null && parametros.length > 0) {
			for (int i = 0; i < parametros.length; i++) {
				query.setParameter(i + 1, parametros[i]);
			}
		}
		return query;
	}

	/**
	 * Executa um hql com o entitymanager corrente.
	 * 
	 * @param em
	 *            EntityManager para realização da operação.
	 * @param hql
	 *            HQL a ser executado.
	 * @param parametros
	 *            Parâmetros a serem setados na query. Opcional.
	 * @return Lista de retorno da query.
	 */
	@SuppressWarnings("rawtypes")
	protected List executeQuery(EntityManager em, String hql,
			Object... parametros) {
		Query query = createQuery(em, hql, parametros);
		return query.getResultList();
	}

	/**
	 * Executa um hql com o entitymanager corrente.
	 * 
	 * @param em
	 *            EntityManager para realização da operação.
	 * @param hql
	 *            HQL a ser executado.
	 * @param maxRegistros
	 *            Número máximo de registro a ser retornado.
	 * @param posPrimeiroRegistro
	 *            Posição do primeiro registro a ser devolvido.
	 * @param parametros
	 *            Parâmetros a serem setados na query. Opcional.
	 * @return Lista de retorno da query.
	 */
	@SuppressWarnings("rawtypes")
	protected List executeQuery(EntityManager em, String hql,
			int posPrimeiroRegistro, int maxRegistros, Object... parametros) {
		Query query = createQuery(em, hql, parametros);
		query.setMaxResults(maxRegistros);
		query.setFirstResult(posPrimeiroRegistro);
		return query.getResultList();
	}

	/**
	 * Verifica se ambas as listas possuem os mesmos objetos. A ordem não é
	 * verificada.
	 */
	public static void assertContentEqual(List<?> list1, List<?> list2) {
		assertTrue(
				"A primeira lista não contem todos os objetos da segunda lista",
				list1.containsAll(list2));
		assertTrue(
				"A segunda lista não contem todos os objetos da primeira lista",
				list2.containsAll(list1));
	}

	/**
	 * Verifica se ambas as listas não possuem os mesmos objetos. A ordem não é
	 * verificada.
	 */
	public static void assertContentNotEqual(List<?> list1, List<?> list2) {
		boolean hqlContemQBE = list1.containsAll(list2);
		boolean qbeContemHQL = list2.containsAll(list1);
		assertFalse("As listas deveriam conter dados equivalentes.",
				hqlContemQBE && qbeContemHQL);
	}

	/**
	 * Verifica se a coleção é diferente de null e não vazia.
	 * 
	 * @param collection
	 *            Para validação.
	 */
	protected void assertNotEmpty(Collection<?> collection) {
		assertTrue(collection != null && !collection.isEmpty());
	}

	/**
	 * Busca a primeira ocorrencia de uma entidade na base de dados.
	 * 
	 * @param tipo
	 *            Tipo da entidade para busca.
	 * @return Objeto encontrado na posição 1.
	 */
	@SuppressWarnings("unchecked")
	protected <TIPO> TIPO getAny(Class<TIPO> tipo) {
		String hql = "from " + tipo.getSimpleName();
		Query query = getJpa().getEm().createQuery(hql);
		query.setMaxResults(1);
		return (TIPO) query.getSingleResult();
	}

	/**
	 * Busca a ocorrencia de uma entidade na base de dados de acordo com uma
	 * posição informada.
	 * 
	 * @param tipo
	 *            Tipo da entidade para busca.
	 * @param posicao
	 *            Posição para identificação do registro na base de dados.
	 * @return Objeto encontrado na posição informada.
	 */
	@SuppressWarnings("unchecked")
	protected <TIPO> TIPO getInPosition(Class<TIPO> tipo, int posicao) {
		String hql = "from " + tipo.getSimpleName();
		Query query = getJpa().getEm().createQuery(hql);
		query.setMaxResults(1);
		query.setFirstResult(posicao);
		return (TIPO) query.getSingleResult();
	}

}
