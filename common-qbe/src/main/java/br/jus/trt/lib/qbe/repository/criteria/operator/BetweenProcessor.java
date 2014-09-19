package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.exception.OperatorException;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class BetweenProcessor extends CriteriaOperatorProcessorBase<Object> {

	@Override
	protected void executeOperation(String property, Operator<Object> operator, Object... values) {
		getJunction().add(Restrictions.between(property, values[0], values[1]));
	}

	/**
	 * Para este operador devem ser informados exatamente 2 valores, ambos do tipo Date ou Number.
	 */
	@Override
	protected void validate(String property, Operator<Object> operator, Object[] values) {
		super.validate(property, operator, values);
		if (values.length != 2 
				&& (!(OperatorProcessorUtil.isDate(values[0]) && OperatorProcessorUtil.isDate(values[1])) // é data
						|| !(OperatorProcessorUtil.isNumber(values[0]) && OperatorProcessorUtil.isNumber(values[1]) // ou é número
								))) {
			throw new OperatorException("O operador Between exige a informação de 2 parâmetros do tipo Date ou Number.");
		}
	}
}
