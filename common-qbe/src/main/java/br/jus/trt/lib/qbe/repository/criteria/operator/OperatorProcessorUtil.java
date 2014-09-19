package br.jus.trt.lib.qbe.repository.criteria.operator;

import java.util.Date;

/**
 * Classe local utilitária com alguns métodos comumente utilizados entre os operadores.
 * @author augusto
 */
class OperatorProcessorUtil {

	/**
	 * Método que verifica se um objeto é do tipo Date.
	 * Auxilizar para os demais operadores.
	 * @param object Valor.
	 * @return true se for uma data.
	 */
	static boolean isDate(Object object) {
		return object != null && (object instanceof Date);
	}
	
	/**
	 * Método que verifica se um objeto é do tipo Number.
	 * Auxilizar para os demais operadores.
	 * @param object Valor.
	 * @return true se for um valor numérico.
	 */	
	static boolean isNumber(Object object) {
		return object != null && (object instanceof Number);
	}
	
}
