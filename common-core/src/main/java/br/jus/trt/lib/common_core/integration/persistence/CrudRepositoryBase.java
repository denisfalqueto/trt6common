package br.jus.trt.lib.common_core.integration.persistence;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Id;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.exception.AppException;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.common_core.util.ReflectionUtil;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.SortConfig;
import br.jus.trt.lib.qbe.api.SortConfig.SortType;
import br.jus.trt.lib.qbe.api.operator.Operators;
import java.io.Serializable;
import javax.inject.Inject;
import org.apache.deltaspike.data.api.EntityRepository;

/**
 * Querier Object para entidades de domínio. Expõe apenas operações para recuperação de 
 * dados. 
 * @author augusto
 *
 */
@SuppressWarnings("serial")
public abstract class CrudRepositoryBase <ENTITY extends Entity<PK>, PK extends Serializable> 
					implements CrudRepository<ENTITY, PK>, EntityRepository<ENTITY, PK> {

    @Inject
	private QBERepository qbeRepository;
	
	private Class<? extends ENTITY> entityClass;

	@Override
	public ENTITY findBy(PK id, String... fetch) {
		
		Field idField = getIdField(getEntityClass());
		
		Filter<ENTITY> filtro = new QBEFilter<ENTITY>(getEntityClass());
		filtro.filterBy(idField.getName(), Operators.equal(), id);
		
		// adicionando propriedades para fetch
		if (fetch != null) {
			for (String f : fetch) {
				if (f != null) {
					filtro.addFetch(f);
				}	
			}
		}
	
		return findBy(filtro);
	}
	
	/**
	 * @see QuerierObject#find(Filter)
	 */
	@Override
	public ENTITY findBy(Filter<? extends ENTITY> filter) throws NonUniqueEntityException {
		
		List<ENTITY> list = findAllBy(filter);
		
		if (list == null || list.isEmpty()) {
			return null;
		} else if (list.size() > 1) {
			throw new NonUniqueEntityException();
		}
		return list.get(0);
	}
	
	/**
	 * @see QuerierObject#list(boolean, String...)
	 */
	@Override
	public List<ENTITY> findAll(boolean ascedant, String... orderBy) {
		SortConfig.SortType tipoOrdenacao = ascedant ? SortType.ASCENDANT : SortType.DESCENDANT;
		
		QBEFilter<ENTITY> filtro = new QBEFilter<ENTITY>(getEntityClass());
		if (orderBy != null) {
			for (String prop : orderBy) {
				filtro.sortBy(new SortConfig(prop, tipoOrdenacao));
			}
		}
		
		return getQbeRepository().search(filtro);
	}
	
	/**
	 * @see QuerierObject#count(Filter)
	 */
	@Override
	public Long count(Filter<? extends ENTITY> filter) {
		long count = getQbeRepository().count(filter);
		return count;
	}
	
	/**
	 * @see QuerierObject#search(Filter)
	 */
	@Override
	public List<ENTITY> findAllBy(Filter<? extends ENTITY> filter) {
		List<ENTITY> search = getQbeRepository().search(filter);
		return search;
	}
	
	/**
	 * @return Repositório QBE para operações de consultas dinâmicas.
	 */
	protected QBERepository getQbeRepository() {
		return qbeRepository;
	}

	/**
	 * @return O tipo da entidade associada a este 
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends ENTITY> getEntityClass() {
		if (this.entityClass == null) {
			List<Class<?>> typeArguments = JavaGenericsUtil.getGenericTypedArguments(CrudRepositoryBase.class, this.getClass());
			this.entityClass = (Class<? extends ENTITY>) typeArguments.get(0);
		}
		
		return this.entityClass;
	}
	
	/**
	 * Recupera o campo que representa o ID da entidade.
	 * @param clazz Tipo da entidade.
	 * @return Campo ID da entidade.
	 */
	@SuppressWarnings("rawtypes")
	protected Field getIdField(Class<? extends Entity> clazz) {
		List<Field> idFields = ReflectionUtil.getFields(clazz, Id.class);
		if (idFields == null || idFields.size() != 1) {
			throw new AppException("Não foi possível identificar um Id para a entidade " + clazz.getSimpleName());
		}
		
		return idFields.get(0);
	}
}
