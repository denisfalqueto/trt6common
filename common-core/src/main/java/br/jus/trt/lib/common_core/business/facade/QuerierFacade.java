package br.jus.trt.lib.common_core.business.facade;

import java.util.List;

import br.jus.trt.lib.common_core.business.bobject.NonUniqueEntityException;
import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.qbe.api.Filter;

/**
 * Fachada 
 * @author augusto
 */
public interface QuerierFacade <ENTITY extends Entity<?>> extends Facade {

	/**
	 * Busca uma entidade por chave primária.
	 * @param id Chave primária da entidade.
	 * @return Entidade correspondente à chave primária.
	 */
	public ENTITY find(Object id);
	
	/**
	 * Busca uma entidade por ID, permitindo provocar o pré-carregamento otimizado de associações
	 * relacionadas (fetch).
	 * @param id Identificador da entidade para busca.
	 * @param fetch Lista de atributos para pré-carregamento.
	 * @return A entidade identificada pelo id informado, ou null caso não seja encontrada.
	 */
	public ENTITY find(Object id, String... fetch);

	/**
	 * Realiza uma consulta com filtro, retornando apenas um elemento. O filtro utilizado deverá garantir que
	 * a consulta resultará em apenas um registro, caso mais de um registro seja encontrado uma exceção será lançada.
	 * @param filter Classe que encapsula os parâmetros de consulta.
	 * @return Elemento resultante da consulta, ou null caso não encontre.
	 */
	public ENTITY find(Filter<? extends ENTITY> filter) throws NonUniqueEntityException;

	/**
	 * Lista todos os registros da entidade associada a este DAO.
	 * @return todos os registros da entidade.
	 */
	public List<ENTITY> list();

	/**
	 * Lista todos os registros da entidade associada de forma ordenada.
	 * 
	 * @param ascedant true para listagem ascendente, false para listagem descendente
	 * 
	 * @param orderBy Lista de campos que serão utilizados na ordenação. A ordem
	 * dos parametros indicará a prioridade da ordenação.
	 * 
	 * @return Lista de registros ordenados.
	 */
	public abstract List<ENTITY> list(boolean ascedant, String... orderBy);
	
	/**
	 * Conta a quantidade de registros da entidade associada a este DAO.
	 * @return a quantidade de registros.
	 */
	public Long count();
	
	/**
	 * Conta a quantidade de registros da entidade.
	 * @param filter Encapsula os parâmetros e configurações das restrições da consulta.
	 * @return a quantidade de registros da entidade.
	 */
	public Long count(Filter<? extends ENTITY> filter);
	
	/**
	 * Conta a quantidade de registros da entidade utilizando um objeto como exemplo para filtro.
	 * @param entity Objeto de exemplo para configuração da consulta.
	 * @return a quantidade de registros da entidade.
	 */
	public Long count(ENTITY entity);
	
	/**
	 * Realiza uma consulta baseada nas configurações do filtro.
	 * @param filter Encapsula os parâmetros e configurações das restrições da consulta.
	 * @return Resultado da consulta.
	 */
	public List<ENTITY> search(Filter<? extends ENTITY> filter);
	
	/**
	 * Realiza uma consulta utilizando um objeto como exemplo para filtro.
	 * @param example Objeto de exemplo para configuração da consulta.
	 * @return Resultado da consulta.
	 */	
	public List<ENTITY> search(ENTITY example);
	
}
