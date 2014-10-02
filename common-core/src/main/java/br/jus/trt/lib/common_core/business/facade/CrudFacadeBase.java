package br.jus.trt.lib.common_core.business.facade;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.integration.persistence.CrudRepository;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.qbe.api.Filter;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.metamodel.SingularAttribute;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

@Transactional
@BusinessExceptionHandler
public abstract class CrudFacadeBase<E extends Entity<PK>, PK extends Serializable>
        implements CrudFacade<E, PK> {

    private Class<CrudRepository<E, PK>> repositoryClass;
    private CrudRepository<E, PK> repository;
    @Inject
    private BeanProvider beanProvider;

    protected Class<CrudRepository<E, PK>> getRepositoryClass() {
        if (repositoryClass == null) {
            List<Class<?>> typeArguments = JavaGenericsUtil.getGenericTypedArguments(CrudFacadeBase.class, this.getClass());
            this.repositoryClass = (Class<CrudRepository<E, PK>>) typeArguments.get(0);
        }
        return this.repositoryClass;
    }

    protected CrudRepository<E, PK> getRepository() {
        if (repository == null) {
            repository = (CrudRepository<E, PK>) beanProvider.getContextualReference(getRepositoryClass());
        }
        return repository;
    }

    @Override
    public E save(E entity) {
        return getRepository().save(entity);
    }

    @Override
    public E saveAndFlush(E entity) {
        return getRepository().saveAndFlush(entity);
    }

    @Override
    public E saveAndFlushAndRefresh(E entity) {
        return getRepository().saveAndFlushAndRefresh(entity);
    }

    @Override
    public void remove(E entity) {
        getRepository().remove(entity);
    }

    @Override
    public void removeAndFlush(E entity) {
        getRepository().removeAndFlush(entity);
    }

    @Override
    public void refresh(E entity) {
        getRepository().refresh(entity);
    }

    @Override
    public void flush() {
        getRepository().flush();
    }

    @Override
    public E findBy(PK primaryKey) {
        return getRepository().findBy(primaryKey);
    }

    @Override
    public List<E> findAll() {
        return getRepository().findAll();
    }

    @Override
    public List<E> findAll(int start, int max) {
        return getRepository().findAll(start, max);
    }

    @Override
    public List<E> findBy(E example, SingularAttribute<E, ?>... attributes) {
        return getRepository().findBy(example, attributes);
    }

    @Override
    public List<E> findBy(E example, int start, int max, SingularAttribute<E, ?>... attributes) {
        return getRepository().findBy(example, start, max, attributes);
    }

    @Override
    public List<E> findByLike(E example, SingularAttribute<E, ?>... attributes) {
        return getRepository().findByLike(example, attributes);
    }

    @Override
    public List<E> findByLike(E example, int start, int max, SingularAttribute<E, ?>... attributes) {
        return getRepository().findByLike(example, start, max, attributes);
    }

    @Override
    public Long count() {
        return getRepository().count();
    }

    @Override
    public Long count(E example, SingularAttribute<E, ?>... attributes) {
        return getRepository().count(example, attributes);
    }

    @Override
    public Long countLike(E example, SingularAttribute<E, ?>... attributes) {
        return getRepository().countLike(example, attributes);
    }

    @Override
    public E findBy(PK id, String... fetch) {
        return getRepository().findBy(id, fetch);
    }

    @Override
    public E findBy(Filter<? extends E> filter) {
        return getRepository().findBy(filter);
    }

    @Override
    public List<E> findAll(boolean ascedant, String... orderBy) {
        return getRepository().findAll(ascedant, orderBy);
    }

    @Override
    public Long count(Filter<? extends E> filter) {
        return getRepository().count(filter);
    }

    @Override
    public List<E> findAllBy(Filter<? extends E> filter) {
        return getRepository().findAllBy(filter);
    }

}
