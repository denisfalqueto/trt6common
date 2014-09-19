package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class SizeNotEqualProcessor extends CriteriaOperatorProcessorBase<Integer> {

	@Override
	protected void executeOperation(String propriedade, Operator<Integer> operator, Integer...valores) {
		getJunction().add(Restrictions.sizeNe(propriedade, valores[0]));
	}
	
}
