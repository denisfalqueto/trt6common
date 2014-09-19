package br.jus.trt.lib.qbe.repository.criteria.operator;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.api.operator.Util;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class InProcessor extends CriteriaOperatorProcessorBase<Object> {

	@Override
	protected void executeOperation(String propriedade, Operator<Object> operator, Object... valores) {
		
		List<Object> listIn = Util.extrairValores(valores);
		
		if (listIn != null && !listIn.isEmpty()) {
			getJunction().add(Restrictions.in(propriedade, listIn));	
		}
	}
	
}
