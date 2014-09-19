package br.jus.trt.lib.common_core.integration.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.util.Parameter;

/**
 * Implementação concreta de um DAO que acessa dados em uma base de dados via JPA. 
 * @author augusto
 * @param <ENTITY> Tipo da entidade de domínio associada.
 */
@SuppressWarnings("serial")
public abstract class JpaDao <ENTITY extends Entity<?>> implements Dao<ENTITY> {

	@Override
	public ENTITY find(Object id) {
		return getEntityManager().find(getEntityType(), id);
	}

	@Override
	public List<ENTITY> list() {
		CriteriaQuery<ENTITY> criteria = getEntityManager().getCriteriaBuilder().createQuery(getEntityType());
		Root<ENTITY> from = criteria.from(getEntityType());
		criteria.select(from);
		
		TypedQuery<ENTITY> query = getEntityManager().createQuery(criteria);
		return query.getResultList();
	}

	@Override
	public Long count() {
		CriteriaBuilder qb = getEntityManager().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = qb.createQuery(Long.class);
		Root<ENTITY> from = criteria.from(getEntityType());
		criteria.select(qb.count(from));
		
		TypedQuery<Long> query = getEntityManager().createQuery(criteria);
		return query.getSingleResult();
	}

	@Override
	public void delete(ENTITY entity) {
		delete(entity.getId());
	}

	@Override
	public void delete(Object id) {
		ENTITY entidade = getEntityManager().getReference(getEntityType(), id);
		getEntityManager().remove(entidade);
		flush();
	}

	@Override
	public void insert(ENTITY entity) {
		getEntityManager().persist(entity);
		flush();
	}

	@Override
	public void update(ENTITY entity) {
		getEntityManager().merge(entity);
		flush();
	}
	
	/**
	 * Para realização de consultas baseadas em HQL com parâmetros "ordenados".
	 * Este método permite a execução de NamedQueries ou HQL diretos. 
	 * 
	 * @param query Nome de uma NamedQuery (devidamente mapeada) ou o próprio HQL para execução. 
	 * Deve utilizar parâmetros ordenados (através do operador "?")
	 * 
	 * @param parameters Parametros ordenados, segundo a ordem esperada pela query.
	 */
	@SuppressWarnings("rawtypes")
	protected List search(String query, Object ... parameters) {
		Query q = prepareOrderedParameterQuery(query, parameters);
		return q.getResultList();
	}
	
	/**
	 * Para realização de consultas baseadas em HQL com parâmetros "identificados".
	 * Este método permite a execução de NamedQueries ou HQL diretos. 
	 * 
	 * @param query Nome de uma NamedQuery (devidamente mapeada) ou o próprio HQL para execução. 
	 * Deve utilizar parâmetros identificados por nomes (através do operador ":nomeParam")
	 * 
	 * @param parameters Parametros identificados. Todos os parâmetros da query devem ser informados
	 */
	@SuppressWarnings("rawtypes")
	protected List search(String query, Parameter ... parameters) {
		Query q = prepareIndentifiedParameterQuery(query, parameters);
		return q.getResultList();
	}
	
	/**
	 * Prepara query com parâmertros identificados com um nome 
	 */
	private Query prepareIndentifiedParameterQuery(String query, Parameter... parameters) {
		Query q = createQuery(query);
		
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				Parameter parametro = parameters[i];
				q.setParameter(parametro.getName(), parametro.getValue());
			}
		}
		return q;
	}	
	
	/**
	 * Prepara query com parâmetros ordenados
	 */
	private Query prepareOrderedParameterQuery(String query, Object... parameters) {
		Query q = createQuery(query);
		
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				Object parametro = parameters[i];
				q.setParameter(i+1, parametro);
			}
		}
		return q;
	}
	
	/**
	 * Busca por uma NamedQuery mapeada o nome informado. Caso não encontre,
	 * considera que a String representa um HQL
	 * @param query O nome de uma NamedQuery ou uma Query HQL para executar 
	 */
	private Query createQuery(String query) {
		Query q = null;
		
		if (query.toLowerCase().contains("from ".toLowerCase())) {
			q = getEntityManager().createQuery(query);	
		} else {
			
			q = getEntityManager().createNamedQuery(query);
		}
		
		return q;
	}
	
	/**
	 * Método responsável por executar o flush do EntityManager 
	 * associado ao DAO.
	 */
	public void flush() {
		getEntityManager().flush();
	}

	/**
	 * @return EntityManager para operações de consulta e persistência de dados.
	 */
	protected abstract EntityManager getEntityManager();
	
	/**
	 * @return Tipo da entidade associada a este DAO. Necessário para permitir a esecução de algumas
	 * operações de consulta e persistência.
	 */
	protected abstract Class<ENTITY> getEntityType();
}
