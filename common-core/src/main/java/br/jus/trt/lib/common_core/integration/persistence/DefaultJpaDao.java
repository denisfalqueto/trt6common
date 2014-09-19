package br.jus.trt.lib.common_core.integration.persistence;

import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;

import br.jus.trt.lib.common_core.business.domain.Entity;

/**
 * Implementação concreta de um DAO JPA, criada para minimizar a necessidade de criação
 * de DAOs concretos para cada entidade de domínio.
 * @author augusto
 *
 */
@SuppressWarnings("serial")
@Default // Default qualifier
public class DefaultJpaDao <ENTITY extends Entity<?>> extends JpaDao<ENTITY>{

	private EntityManager entityManager;
	private Class<ENTITY> entityType;

	public DefaultJpaDao(EntityManager entityManager, Class<ENTITY> entityType) {
		super();
		this.entityManager = entityManager;
		this.entityType = entityType;
	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	protected Class<ENTITY> getEntityType() {
		return entityType;
	}

}
