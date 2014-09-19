package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "is empty". Checa se uma relação 1-* não está vazia (coleção).
 */
@SuppressWarnings("serial")
public class IsNotEmpty extends OperatorBase<Object> {

	/**
	 * Retorna sempre -1, visto que este operador não necessita de valor
	 */
	@Override
	public int getMandatoryValuesNumber() {
		return -1;
	}
}
