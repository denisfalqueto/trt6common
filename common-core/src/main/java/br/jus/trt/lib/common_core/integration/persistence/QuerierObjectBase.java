package br.jus.trt.lib.common_core.integration.persistence;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Id;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.exception.AppException;
import br.jus.trt.lib.common_core.integration.persistence.Dao;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.common_core.util.ReflectionUtil;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.SortConfig;
import br.jus.trt.lib.qbe.api.SortConfig.SortType;
import br.jus.trt.lib.qbe.api.operator.Operators;

/**
 * Querier Object para entidades de domínio. Expõe apenas operações para recuperação de 
 * dados. 
 * @author augusto
 *
 */
@SuppressWarnings("serial")
public abstract class QuerierObjectBase <ENTITY extends Entity<?>> 
					extends BObjectBase<ENTITY> implements QuerierObject<ENTITY> {

	private QBERepository qbeRepository;
	
	private Class<? extends ENTITY> entityClass;
	
	/**
	 * @param dao DAO para operações de persistência de dados.
	 * @param qbeRepository Mecanismo QBE para realização de consultas dinâmicas.
	 */
	public QuerierObjectBase(Dao<ENTITY> dao, QBERepository qbeRepository) {
		super(dao);
		this.qbeRepository = qbeRepository;
	}

	/** 
	 * @see br.jus.trt.lib.common_core.business.bobject.QuerierObject#find(java.lang.Object)
	 */
	@Override
	public ENTITY find(Object id) {
		return getDao().find(id);
	}

	@Override
	public ENTITY find(Object id, String... fetch) {
		
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
	
		return find(filtro);
	}
	
	/**
	 * @see QuerierObject#find(Filter)
	 */
	@Override
	public ENTITY find(Filter<? extends ENTITY> filter) throws NonUniqueEntityException {
		
		List<ENTITY> list = search(filter);
		
		if (list == null || list.isEmpty()) {
			return null;
		} else if (list.size() > 1) {
			throw new NonUniqueEntityException();
		}
		return list.get(0);
	}
	
	/**
	 * @see br.jus.trt.lib.common_core.business.bobject.QuerierObject#list()
	 */	
	@Override
	public List<ENTITY> list() {
		return getDao().list();
	}
	
	/**
	 * @see QuerierObject#list(boolean, String...)
	 */
	@Override
	public List<ENTITY> list(boolean ascedant, String... orderBy) {
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
	 * @see QuerierObject#count()
	 */
	@Override
	public Long count() {
		return getDao().count();
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
	 * @see QuerierObject#count(Entity)
	 */
	@Override
	public Long count(ENTITY entity) {
		return count(new QBEFilter<ENTITY>(entity));
	}
	
	/**
	 * @see QuerierObject#search(Filter)
	 */
	@Override
	public List<ENTITY> search(Filter<? extends ENTITY> filter) {
		List<ENTITY> search = getQbeRepository().search(filter);
		return search;
	}
	
	/**
	 * @see QuerierObject#search(Entity)
	 */
	@Override
	public List<ENTITY> search(ENTITY example) {
		return search(new QBEFilter<ENTITY>(example));
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
			List<Class<?>> typeArguments = JavaGenericsUtil.getGenericTypedArguments(BObjectBase.class, this.getClass());
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
