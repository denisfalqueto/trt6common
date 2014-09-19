package br.jus.trt.lib.common_core.business.facade;

import br.jus.trt.lib.common_core.business.bobject.CrudBObject;
import br.jus.trt.lib.common_core.business.domain.Entity;

public interface CrudFacade <ENTITY extends Entity<?>> extends QuerierFacade<ENTITY>{

	/**
	 * Método responsável por excluir uma entidade.
	 * @param entity Entidade que será excluída.
	 */
	public void delete(ENTITY entity);
	
	/**
	 * Exclui uma entidade da base de dados.
	 * @param id Identificador da entidade a ser excluída.
	 */
	public void delete(Object id);
	
	/**
	 * Atualiza os dados de uma entidade na base de dados.
	 * @param entity Objeto com dados atualizados.
	 */
	public void update(ENTITY entity);
	
	/**
	 * Insere uma entidade na base de dados.
	 * @param entity Entidade que será inserida.
	 */
	public void insert(ENTITY entity);

	/**
	 * Insere ou atualiza uma entidade na base de dados. O tipo de operação a ser realizada será verificada
	 * no momento da execução através do método {@link CrudBObject#isInsertion(Entity)}.
	 * @param entity Entidade que será excluída.
	 */
	public void save(ENTITY entity);
	
}
