package br.jus.trt.lib.common_tests;

import static org.junit.Assert.fail;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import br.jus.trt.lib.common_tests.jpa.JPAStandalone;

/**
 * Classe base com comportamento comum para testes unitários que utilizam um
 * contexto transacional. Gerencia o ciclo de vida do JUnit, JPA, suporta CDI, e
 * outros recursos necessários para testes.
 * 
 * Cada método de teste é isolado em uma transação, não interferindo um no outro. Ao final de cada método,
 * é realizado rollback na transação.
 * 
 * @author Augusto
 * 
 */
@Ignore
public class LocalTransactionTestBase extends TestBase {

	/** para controle de transações e acesso à base de dados */
	private JPAStandalone jpaInstance;

	/**
	 * Executado antes de cada método de teste. Realiza o controle do início da
	 * transação.
	 */
	@Before
	public void beforeEachTest() {
		jpaInstance.startSession();
		jpaInstance.startTransaction();
	}

	/**
	 * Executado após de cada método de teste. Realiza o controle do término da
	 * transação.
	 */
	@After
	public void afterEachTest() {
		jpaInstance.rollbackTransaction();
		jpaInstance.closeSession();
	}

	public EntityManager getEntityManager() {
		return jpaInstance.getEm();
	}

	public JPAStandalone getJpa() {
		return jpaInstance;
	}

	@Inject
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
	 * @param hql
	 *            HQL a ser executado.
	 * @param parametros
	 *            Parâmetros a serem setados na query. Opcional.
	 * @return Lista de retorno da query.
	 */
	protected Long executeCountQuery(String hql, Object... parametros) {
		return executeCountQuery(getEntityManager(), hql, parametros);
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
	 * @param hql
	 *            HQL para criação da query.
	 * @param parametros
	 *            Parâmetros a serem adicionados à query.
	 * @return Query criada.
	 */
	protected Query createQuery(String hql, Object... parametros) {
		return createQuery(getEntityManager(), hql, parametros);
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
	 * @param hql
	 *            HQL a ser executado.
	 * @param parametros
	 *            Parâmetros a serem setados na query. Opcional.
	 * @return Lista de retorno da query.
	 */
	@SuppressWarnings("rawtypes")
	protected List executeQuery(String hql, Object... parametros) {
		return executeQuery(getEntityManager(), hql, parametros);
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
	protected List executeQuery(String hql,
			int posPrimeiroRegistro, int maxRegistros, Object... parametros) {
		return executeQuery(getEntityManager(), hql, posPrimeiroRegistro, maxRegistros, parametros);
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
	 * Busca a primeira ocorrencia de uma entidade na base de dados.
	 * 
	 * @param tipo
	 *            Tipo da entidade para busca.
	 * @return Objeto encontrado na posição 1.
	 */
	@SuppressWarnings("unchecked")
	protected <TIPO> TIPO getAny(Class<TIPO> tipo) {
		String hql = "from " + tipo.getSimpleName();
		Query query = getEntityManager().createQuery(hql);
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
		Query query = getEntityManager().createQuery(hql);
		query.setMaxResults(1);
		query.setFirstResult(posicao);
		return (TIPO) query.getSingleResult();
	}

}
