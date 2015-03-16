package br.jus.trt.lib.common_core.business.facade;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.logging.log4j.Logger;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.integration.persistence.CrudRepository;
import br.jus.trt.lib.common_core.util.DIContainerUtil;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.qbe.api.Filter;

@SuppressWarnings("serial")
@BusinessExceptionHandler
public abstract class CrudFacadeBase<CR extends CrudRepository<E, PK>, E extends Entity<PK>, PK extends Serializable>
        implements CrudFacade<E, PK> {

    @Inject
    protected Logger log;
    
    private CR repository;

    protected CrudRepository<E, PK> getRepository() {
        if (repository == null) {
            List<Class<?>> genericsTypedArguments = JavaGenericsUtil.getGenericTypedArguments(CrudFacadeBase.class, this.getClass());
            @SuppressWarnings("unchecked")
			Class<CR> repositoryType = (Class<CR>) genericsTypedArguments.get(0); // o repositório é o primeiro parâmetro genérico
            repository = new DIContainerUtil().lookup(repositoryType);
        }
        return repository;
    }

    @Override
    public E save(E entity) {
        return getRepository().saveAndFlush(entity);
    }

    @Override
    public void remove(E entity) {
        log.entry();
        getRepository().removeAndFlush(entity);
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
