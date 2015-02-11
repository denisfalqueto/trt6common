package br.jus.trt.lib.common_core.integration.persistence;

import java.io.Serializable;
import java.util.List;

import org.apache.deltaspike.data.api.EntityRepository;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.qbe.api.Filter;

import com.mysema.query.types.Path;

/**
 * Interface para operações de consulta de dados orientado à uma entidade de domínio.
 * @author augusto
 *
 * @param <ENTITY>
 */
public interface CrudRepository<ENTITY extends Entity<PK>, PK extends Serializable> extends EntityRepository<ENTITY, PK> {

	/**
	 * Busca uma entidade por ID, permitindo provocar o pré-carregamento otimizado de associações
	 * relacionadas (fetch).
	 * @param id Identificador da entidade para busca.
	 * @param fetch Lista de atributos para pré-carregamento.
	 * @return A entidade identificada pelo id informado, ou null caso não seja encontrada.
	 */
	public ENTITY findBy(PK id, Path... fetch);

	/**
	 * Realiza uma consulta com filtro, retornando apenas um elemento. O filtro utilizado deverá garantir que
	 * a consulta resultará em apenas um registro, caso mais de um registro seja encontrado uma exceção será lançada.
	 * @param filter Classe que encapsula os parâmetros de consulta.
	 * @return Elemento resultante da consulta, ou null caso não encontre.
	 * @throws NonUniqueEntityException Exceção lançada quando a consulta retorna mais de um objeto.
	 */
	public ENTITY findBy(Filter<? extends ENTITY> filter) throws NonUniqueEntityException;
	
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
	public abstract List<ENTITY> findAll(boolean ascedant, Path... orderBy);
	
	/**
	 * Conta a quantidade de registros da entidade.
	 * @param filter Encapsula os parâmetros e configurações das restrições da consulta.
	 * @return a quantidade de registros da entidade.
	 */
	public Long count(Filter<? extends ENTITY> filter);
	
	/**
	 * Realiza uma consulta baseada nas configurações do filtro.
	 * @param filter Encapsula os parâmetros e configurações das restrições da consulta.
	 * @return Resultado da consulta.
	 */
	public List<ENTITY> findAllBy(Filter<? extends ENTITY> filter);
	
}