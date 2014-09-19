package br.jus.trt.lib.qbe.api;

import java.io.Serializable;

/**
 * Definição de Operador, que representa uma operação a ser realizada sobre uma ou mais propriedades de 
 * uma entidade consultável.
 * @author augusto
 *
 */
public interface Operator<VALOR> extends Serializable, Cloneable {

	/**
	 * @return true caso este operador aceite um valor "null". Geralmente ocorre quando sua execução ocorre apenas sobre
	 * a própria propriedade. Ex: isNull(), isNotNull()
	 */
	public abstract boolean isNullValueAllowed();

	/**
	 * @return Um clone deste {@link Operator}.
	 */
	public abstract Object clone() throws CloneNotSupportedException;

	/**
	 * A maioria das operações devem receber valores como parâmetros de entrada, como exceção de algumas, como: isNull, isEmpty, etc. 
	 * Este método permite a configuração da obrigatoriedade de informar uma certa quantidade mínima de valores em uma restrição. Um valor
	 * negativo (&lt;0) deve representa a não obrigatoriedade de informar valores.
	 * @return Número de valores mínimos obrigarórios para esta operação, sendo "0" a represatação da não obrigatoriedade.
	 */
	public abstract int getMandatoryValuesNumber();

	
}