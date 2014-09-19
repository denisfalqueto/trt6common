package br.jus.trt.lib.common_core.business.bobject;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.integration.persistence.Dao;


/**
 * BusinessObject - Base para as Classes de negócio.
 * @author augusto
 *
 * @param <ENTITY> Tipo da entidade de domínio associada a este BO.
 */
@SuppressWarnings("serial")
public abstract class BObjectBase <ENTITY extends Entity<?>> implements BObject {

	private final Dao<ENTITY> dao;

	/**
	 * @param dao DAO para operações de persistência de dados.
	 */
	public BObjectBase(Dao<ENTITY> dao) {
		super();
		this.dao = dao;
	}

	/**
	 * Retorna a implementação de DAO associada a este manager.
	 */
	protected Dao<ENTITY> getDao() {
		return dao;
	}

}
