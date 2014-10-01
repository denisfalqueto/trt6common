package br.jus.trt.lib.common_core.business.facade;

import br.jus.trt.lib.common_core.integration.persistence.CrudBObject;
import br.jus.trt.lib.common_core.business.domain.Entity;

@SuppressWarnings("serial")
public abstract class CrudFacadeBase <ENTITY extends Entity<?>> 
			extends QuerierFacadeBase<ENTITY> implements CrudFacade<ENTITY> {

	@Override
	public void delete(ENTITY entity) {
		getBObject().delete(entity);
	}

	@Override
	public void delete(Object id) {
		getBObject().delete(id);
	}

	@Override
	public void update(ENTITY entity) {
		getBObject().update(entity);
	}

	@Override
	public void insert(ENTITY entity) {
		getBObject().insert(entity);
	}

	@Override
	public void save(ENTITY entity) {
		getBObject().save(entity);
	}

	@Override
	protected abstract CrudBObject<ENTITY> getBObject();
}
