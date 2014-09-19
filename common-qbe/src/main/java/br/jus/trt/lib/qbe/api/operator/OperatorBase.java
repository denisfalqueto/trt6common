package br.jus.trt.lib.qbe.api.operator;

import org.apache.commons.beanutils.PropertyUtils;

import br.jus.trt.lib.qbe.api.Operator;

/**
 * Classe que representa um operador a ser aplicado nas operações de consultas QBE.
 * @author augusto
 *
 * @param <VALOR> Permite a definição do tipo de valores que este operador trabalha.
 */
@SuppressWarnings("serial")
public abstract class OperatorBase<VALOR> implements Cloneable, Operator<VALOR> {

	public boolean isNullValueAllowed() {
		return getMandatoryValuesNumber() < 0;
	}

	/**
	 * @see Operator#getMandatoryValuesNumber()
	 */
	@Override
	public int getMandatoryValuesNumber() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public Object clone() throws CloneNotSupportedException {
		try {
			Operator clone = this.getClass().newInstance();
			PropertyUtils.copyProperties(clone, this); // copia apenas campos com get e set public
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloneNotSupportedException("Falha ao tentar realiazar clone do operador");
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
