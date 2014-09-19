package br.jus.trt.lib.qbe.api;

import java.io.Serializable;


/**
 * Classe que implementa os conceitos de paginação na base de dados:
 * <br/> 
 * Tamanho da página (número de registros) e Posição do primeiro registro.
 * @author augusto
 *
 */
@SuppressWarnings("serial")
public class Pagination implements Serializable {

	/** Número de registros a serem retornados na consulta (representa uma página) */
	private Integer maxResult;
	
	/** Posição do primeiro registro na base de dados */
	private Integer firstResult;
	
	/** Permite a configuração de uma consulta otimística, buscando um número maior de registros 
	 * solicitos, antecipando a provável utilização pelo usuário do recurso de paginação */
	private int optmisticIndex = 1;

	/**
	 * @param maxResult Número de registros a serem retornados na consulta (representa uma página)
	 * @param firstResult Posição do primeiro registro na base de dados
	 */
	public Pagination(Integer firstResult, Integer maxResult) {
		super();
		this.maxResult = maxResult;
		this.firstResult = firstResult;
	}
	
	/**
	 * @param maxResult Número de registros a serem retornados na consulta (representa uma página), a partir do primeiro
	 * registro por padrão.
	 */
	public Pagination(Integer pageSize) {
		super();
		this.maxResult = pageSize;
		this.firstResult = 0;
	}

	public Integer getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	/**
	 * @return true se a paginação estiver configurada.
	 */
	public boolean isActive() {
		return (firstResult != null && firstResult >= 0)
				&& (maxResult != null && maxResult > 0);
	}

	public int getOptmisticIndex() {
		return optmisticIndex;
	}

	public void setOptmisticIndex(int optmisticIndex) {
		this.optmisticIndex = optmisticIndex;
	}
}
