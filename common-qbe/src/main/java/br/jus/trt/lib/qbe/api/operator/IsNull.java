package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "is null";
 */
@SuppressWarnings("serial")
public class IsNull extends OperatorBase<Object> {

	/**
	 * Retorna sempre -1, visto que este operador não necessita de valor
	 */
	@Override
	public int getMandatoryValuesNumber() {
		return -1;
	}
}
