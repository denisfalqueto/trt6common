package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.api.operator.SizeGreaterThan;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class SizeGreaterThanProcessor extends CriteriaOperatorProcessorBase<Integer> {

	@Override
	protected void executeOperation(String propriedade, Operator<Integer> operator, Integer...valores) {
		
		SizeGreaterThan greaterThan = (SizeGreaterThan) operator;
		if (greaterThan.isGreaterEqual()) {
			getJunction().add(Restrictions.sizeGe(propriedade, valores[0]));
		} else {
			getJunction().add(Restrictions.sizeGt(propriedade, valores[0]));
		}
	}	
}
