package br.jus.trt.lib.common_core.integration.persistence;

import java.io.Serializable;
import java.util.List;

import br.jus.trt.lib.common_core.business.domain.Entity;

/**
 * Definição de alto nível de uma classe de acesso à dados, 
 * independente da plataforma de persistência.  
 * @author augusto
 * @param <ENTITY> Tipo da entidade de domínio associada.
 */
public interface Dao<ENTITY extends Entity<?>> extends Serializable {

	/**
	 * Método responsável por consultar uma entidade por chave primária.
	 * @param id Chave primária da entidade.
	 * @return Entidade correspondente à chave primária.
	 */
	public ENTITY find(Object id);
	
	/**
	 * Lista todos os registros da entidade associada a este DAO.
	 * @return todos os registros da entidade.
	 */	
	public List<ENTITY> list();
	
	/**
	 * Conta a quantidade de registros da entidade associada a este DAO.
	 * @return a quantidade de registros.
	 */
	public Long count();
	
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
	 * Método responsável por inserir uma entidade.
	 * @param entity Entidade que será inserida.
	 */
	public void insert(ENTITY entity);
	
	/**
	 * Método responsável por alterar uma entidade.
	 * @param entity Entidade que será alterada.
	 */
	public void update(ENTITY entity);
	
}
