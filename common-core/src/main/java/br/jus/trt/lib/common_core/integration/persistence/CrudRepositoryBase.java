package br.jus.trt.lib.common_core.integration.persistence;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Id;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.logging.log4j.Logger;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.exception.AppException;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.SortConfig;
import br.jus.trt.lib.qbe.api.SortConfig.SortType;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.util.ReflectionUtil;
import javax.persistence.EntityManager;

/**
 * Querier Object para entidades de domínio. Expõe apenas operações para recuperação de 
 * dados. 
 * @author augusto
 *
 */
public abstract class CrudRepositoryBase<ENTITY extends Entity<PK>, PK extends Serializable> 
        implements CrudRepository<ENTITY, PK>, EntityRepository<ENTITY, PK> {

        @Inject
	protected Logger log;

	@Inject
	private QBERepository qbeRepository;
        
        @Inject
        private EntityManager em;

	private Class<? extends ENTITY> entityClass;

        @Override
        public void remove(ENTITY entity) {
            log.entry(entity);
            if (!em.contains(entity)) {
                log.debug("Reinserindo entidade no Entitymanager");
                log.trace(entity);
                entity = em.merge(entity);
            }
            em.remove(entity);
        }

        @Override
        public void removeAndFlush(ENTITY entity) {
            log.entry(entity);
            this.remove(entity);
            em.flush();
        }

	@Override
	public ENTITY findBy(PK id, String... fetch) {
            log.entry(id, fetch);
		
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

		return log.exit(findBy(filtro));
	}

	/**
	 * @see QuerierObject#find(Filter)
	 */
	@Override
	public ENTITY findBy(Filter<? extends ENTITY> filter)
			throws NonUniqueEntityException {
		log.entry(filter);

		List<ENTITY> list = findAllBy(filter);

		if (list == null || list.isEmpty()) {
			return log.exit(null);
		} else if (list.size() > 1) {
			throw new NonUniqueEntityException();
		}
		return log.exit(list.get(0));
	}

	/**
	 * @see QuerierObject#list(boolean, String...)
	 */
	@Override
	public List<ENTITY> findAll(boolean ascedant, String... orderBy) {
		log.entry(ascedant, orderBy);
		SortConfig.SortType tipoOrdenacao = ascedant ? SortType.ASCENDING
				: SortType.DESCENDING;

		QBEFilter<ENTITY> filtro = new QBEFilter<ENTITY>(getEntityClass());
		if (orderBy != null) {
			log.debug("Ordenar o resultado");
			for (String prop : orderBy) {
				filtro.sortBy(new SortConfig(prop, tipoOrdenacao));
			}
		}

		return log.exit(getQbeRepository().search(filtro));
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
		return getQbeRepository().search(filter);
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
			List<Class<?>> typeArguments = JavaGenericsUtil
					.getGenericTypedArguments(CrudRepositoryBase.class,
							this.getClass());
			this.entityClass = (Class<? extends ENTITY>) typeArguments.get(0);
		}

		return this.entityClass;
	}

	/**
	 * Recupera o campo que representa o ID da entidade.
	 * 
	 * @param clazz
	 *            Tipo da entidade.
	 * @return Campo ID da entidade.
	 */
	@SuppressWarnings("rawtypes")
	protected Field getIdField(Class<? extends Entity> clazz) {
		List<Field> idFields = ReflectionUtil.getFields(clazz, Id.class);
		if (idFields == null || idFields.size() != 1) {
			throw new AppException(
					"Não foi possível identificar um Id para a entidade "
							+ clazz.getSimpleName());
		}

		return idFields.get(0);
	}
}
