package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.api.operator.SizeLessThan;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class SizeLessThanProcessor extends CriteriaOperatorProcessorBase<Integer> {

	@Override
	protected void executeOperation(String propriedade, Operator<Integer> operator, Integer...valores) {
		
		SizeLessThan lessThan = (SizeLessThan) operator;
		if (lessThan.isLessEqual()) {
			getJunction().add(Restrictions.sizeLe(propriedade, valores[0]));
		} else {
			getJunction().add(Restrictions.sizeLt(propriedade, valores[0]));
		}
	}
	
}
