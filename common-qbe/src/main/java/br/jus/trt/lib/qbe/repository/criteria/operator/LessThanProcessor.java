package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.api.operator.LessThan;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class LessThanProcessor extends CriteriaOperatorProcessorBase<Object> {

	@Override
	protected void executeOperation(String propriedade, Operator<Object> operator, Object... valores) {
		
		LessThan lessThan = (LessThan) operator;
		if (lessThan.isLessThen()) {
			getJunction().add(Restrictions.le(propriedade, valores[0]));
		} else {
			getJunction().add(Restrictions.lt(propriedade, valores[0]));
		}
	}
	
}
