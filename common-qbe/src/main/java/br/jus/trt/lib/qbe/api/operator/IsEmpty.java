package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "is empty". Checa se uma relação 1-* está vazia (coleção).
 */
@SuppressWarnings("serial")
public class IsEmpty extends OperatorBase<Object> {

	/**
	 * Retorna sempre -1, visto que este operador não necessita de valor
	 */
	@Override
	public int getMandatoryValuesNumber() {
		return -1;
	}
}
