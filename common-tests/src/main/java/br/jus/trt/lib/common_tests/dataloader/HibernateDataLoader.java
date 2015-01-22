package br.jus.trt.lib.common_tests.dataloader;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import br.jus.trt.lib.common_tests.cdi.ForTest;

/**
 * Data loader que utiliza o EntityManager para carregamento dos dados.
 * @author augusto
 *
 */
public abstract class HibernateDataLoader implements DataLoader {

	private Logger logger;
	
	/** Para acesso à conexão com a base de dados. */
	private EntityManager entityManager;
	
	protected Session getSession() {
		return  entityManager != null ? (Session) entityManager.getDelegate() : null;
	}
	
	// getters and setters

	protected Logger getLogger() {
		return logger;
	}

	@Inject
	protected void setLogger(@ForTest Logger logger) {
		this.logger = logger;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@Inject
	protected void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
}
