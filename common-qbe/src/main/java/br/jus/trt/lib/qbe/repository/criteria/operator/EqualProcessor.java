package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class EqualProcessor extends CriteriaOperatorProcessorBase<Object> {

	@Override
	protected void executeOperation(String propriedade, Operator<Object> operator, Object...valores) {
		SimpleExpression eq = Restrictions.eq(propriedade, valores[0]);
		getJunction().add(eq);
	}
	
}
