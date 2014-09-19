package br.jus.trt.lib.qbe.api;

import java.io.Serializable;

import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessor;

/**
 * Representa um repositório de processadores de {@link Operator}s, que representará o mapeamento entre operadores
 * e seus diversos processadores. Isto permitirá que um mesmo operador seja processado sobre diferentes 
 * macanismos ou tecnologias.
 * @author augusto
 *
 */
public interface OperatorProcessorRepository extends Serializable { 

	/**
	 * Registra um processador. Caso já exista um processador para este {@link Operator}, será substituído pelo
	 * este informado via parâmetro.
	 * @param operatorType Tipo de operador a ser registrado.
	 * @param processorType Processador de {@link Operator} a ser registrado.
	 */
	@SuppressWarnings("rawtypes")
	public void register(Class<? extends Operator> operatorType, Class<? extends OperatorProcessor> processorType);
	
	/**
	 * @return Um processador capaz de lidar com o {@link Operator} informado. Null caso não
	 * seja encontrado nenhum compatível.
	 */
	@SuppressWarnings("rawtypes")
	public Class<? extends OperatorProcessor> getProcessor(Class<? extends Operator> operatorType); 
}
