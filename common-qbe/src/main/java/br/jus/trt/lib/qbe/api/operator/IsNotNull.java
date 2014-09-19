package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "is not null";
 */
@SuppressWarnings("serial")
public class IsNotNull extends OperatorBase<Object> {

	/**
	 * Retorna sempre -1, visto que este operador não necessita de valor
	 */
	@Override
	public int getMandatoryValuesNumber() {
		return -1;
	}
}
