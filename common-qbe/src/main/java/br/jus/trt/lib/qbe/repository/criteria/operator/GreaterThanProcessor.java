package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.api.operator.GreaterThan;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class GreaterThanProcessor extends CriteriaOperatorProcessorBase<Object> {

	@Override
	protected void executeOperation(String propriedade, Operator<Object> operator, Object... valores) {
		
		GreaterThan greaterThan = (GreaterThan) operator;
		if (greaterThan.isGreaterEqual()) {
			getJunction().add(Restrictions.ge(propriedade, valores[0]));			
		} else {
			getJunction().add(Restrictions.gt(propriedade, valores[0]));
		}
		
	}
	
}
