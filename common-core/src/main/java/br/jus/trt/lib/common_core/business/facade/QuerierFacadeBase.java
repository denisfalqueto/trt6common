package br.jus.trt.lib.common_core.business.facade;

import java.util.List;

import br.jus.trt.lib.common_core.business.bobject.NonUniqueEntityException;
import br.jus.trt.lib.common_core.business.bobject.QuerierObject;
import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.qbe.api.Filter;

@SuppressWarnings("serial")
@BusinessExceptionHandler
public abstract class QuerierFacadeBase <ENTITY extends Entity<?>> implements QuerierFacade<ENTITY> {

	protected abstract QuerierObject<ENTITY> getBObject();
	
	@Override
	public ENTITY find(Object id) {
		return getBObject().find(id);
	}

	@Override
	public List<ENTITY> list() {
		return getBObject().list();
	}

	@Override
	public Long count() {
		return getBObject().count();
	}

	@Override
	public ENTITY find(Object id, String... fetch) {
		return getBObject().find(id, fetch);
	}

	@Override
	public ENTITY find(Filter<? extends ENTITY> filter) throws NonUniqueEntityException {
		return getBObject().find(filter);
	}

	@Override
	public List<ENTITY> list(boolean ascedant, String... orderBy) {
		return getBObject().list(ascedant, orderBy);
	}

	@Override
	public Long count(Filter<? extends ENTITY> filter) {
		return getBObject().count(filter);
	}

	@Override
	public Long count(ENTITY entity) {
		return getBObject().count(entity);
	}

	@Override
	public List<ENTITY> search(Filter<? extends ENTITY> filter) {
		return getBObject().search(filter);
	}

	@Override
	public List<ENTITY> search(ENTITY example) {
		return getBObject().search(example);
	}

	
}
