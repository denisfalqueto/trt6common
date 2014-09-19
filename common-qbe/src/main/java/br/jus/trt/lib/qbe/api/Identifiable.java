package br.jus.trt.lib.qbe.api;

import java.io.Serializable;

/**
 * Interface que mantem acesso à chave primária de uma entidade de domínio. 
 * @author Fabiano Lúcio de Souza Rolim
 * @param <PK> Tipo da chave primária da entidade.
 */
public interface Identifiable extends Serializable  {

	/**
	 * Recupera o ID da Entidade.
	 * @return ID da Entidade.
	 */
	public abstract Object getId();
	
}