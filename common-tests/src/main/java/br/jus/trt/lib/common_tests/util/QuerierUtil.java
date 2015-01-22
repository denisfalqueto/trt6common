package br.jus.trt.lib.common_tests.util;

import static org.junit.Assert.fail;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Oferece operações genéricas de consulta para testes unitários.
 * @author Augusto
 *
 */
public class QuerierUtil {

	@Inject
	private EntityManager em;
	
	/**
	 * @param type Tipo da entidade a ser retornada.
	 * @return Qualquer entidade do tipo informado.
	 */
	@SuppressWarnings("unchecked")
	public <T> T findAny(Class<T> type) {
		Query query = getEm().createQuery("from " + type.getSimpleName());
		query.setMaxResults(1);
		return (T) query.getSingleResult();
	}
	
	/**
	 * @param type Tipo da entidade a ser retornada.
	 * @param position Posição do registro a ser recuperado (zero based).
	 * @return Qualquer entidade do tipo informado.
	 */
	@SuppressWarnings("unchecked")
	public <T> T findAt(Class<T> type, int position) {
		Query query = getEm().createQuery("from " + type.getSimpleName());
		query.setMaxResults(1);
		query.setFirstResult(position);
		return (T) query.getSingleResult();
	}
	
	/**
	 * @param type Tipo da entidade a ser retornada.
	 * @return Todas os objetos do tipo informado.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(Class<T> type) {
		Query query = getEm().createQuery("from " + type.getSimpleName());
		return query.getResultList();
	}

	/**
	 * 
	 * @param type TIpo da entidade a ser consultada.
	 * @return Número total de registros encontrados na base de dados.
	 */
	public <T> Long count(Class<T> type) {
		return (Long) getEm().createQuery("select count(e) from " + type.getSimpleName() + " e ").getSingleResult();
	}

	/**
	 * Busca objetos de exemplo, validando se o resultado contém pelo menos um
	 * registro.
	 * 
	 * @param hql
	 *            HQL para buscar os objetos.
	 * @param valores
	 *            Valores de filtro no hql
	 * @return Lista de entidades de acordo com o hql informado.
	 */
	public <TIPO> List<TIPO> searchAndValidateNotEmpty(String hql, Object... valores) {
		@SuppressWarnings("unchecked")
		List<TIPO> resultadoHQL = executeQuery(getEm(), hql, valores);

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
	public Long executeCountQuery(String hql, Object... parametros) {
		return executeCountQuery(getEm(), hql, parametros);
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
	public Long executeCountQuery(EntityManager em, String hql,
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
	public Query createQuery(String hql, Object... parametros) {
		return createQuery(getEm(), hql, parametros);
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
	public Query createQuery(EntityManager em, String hql,
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
	public List executeQuery(String hql, Object... parametros) {
		return executeQuery(getEm(), hql, parametros);
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
	public List executeQuery(EntityManager em, String hql,
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
	public List executeQuery(String hql,
			int posPrimeiroRegistro, int maxRegistros, Object... parametros) {
		return executeQuery(getEm(), hql, posPrimeiroRegistro, maxRegistros, parametros);
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
	public List executeQuery(EntityManager em, String hql,
			int posPrimeiroRegistro, int maxRegistros, Object... parametros) {
		Query query = createQuery(em, hql, parametros);
		query.setMaxResults(maxRegistros);
		query.setFirstResult(posPrimeiroRegistro);
		return query.getResultList();
	}	
	
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
}
