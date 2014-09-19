package br.jus.trt.lib.qbe.api;

import java.io.Serializable;
import java.util.List;

/**
 * Determina a interface para uma solução genérica e dinâmica para consultas, baseada no conceito de QBE (Query by Example).
 * Prevê a construção dinamicamente de uma QUERY baseada e um objeto preenchido com informações de consulta. 
 * Ele é capaz de analisar as propriedades do objeto, determinar quais são determinantes para consulta, construí-la e
 * executá-la.
 * 
 * @author augusto
 */
public interface QBERepository extends Serializable{

	
	/**
	 * Realiza consulta dinâmica a partir da análise das propriedades do filtro recebido.
	 * @param filter Objeto preparado com dados de configuração para a geração dinâmica de uma consulta.
	 * @return Resultado da consulta de acordo com os parâmetros de entrada.
	 */
	public <TIPO extends Identifiable> List<TIPO> search(Filter<? extends TIPO> filter);
	
	/**
	 * Realiza consulta dinâmica a partir da análie das propriedades do filtro recebido, retornando o total de registros encontrados.
	 * @param filter Objeto preparado com dados de configuração para a geração dinâmica de uma consulta.
	 * @return Total de registros encontrados de acordo com o filtro informado.
	 */
	public <TIPO extends Identifiable> long count(Filter<? extends TIPO> filter);	

	/**
	 * @return O repositório de processadores de {@link Operator}.
	 */
	public OperatorProcessorRepository getOperatorRepository();
}
