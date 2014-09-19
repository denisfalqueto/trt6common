package br.jus.trt.lib.qbe.repository.criteria;

import java.io.Serializable;
import java.util.List;

/**
 * Para representação do tipo de resultado de consultas utilizado no framework. As consultas retornarão uma lista, que possui
 * o seu total de elementos retornados e o total de elementos na base dados (para consultas paginadas)
 * @author augusto
 *
 * @param <TIPO>
 */
public interface IResult<TIPO extends Object> extends Serializable, List<TIPO> {

	/**
	 * @return Número total de registros resultantes da consulta, desconsiderando a paginação (count)
	 */
	Long getCountResult();

	/**
	 * @return A lista original associada a este Result.
	 */
	List<TIPO> getOriginalList();

}
