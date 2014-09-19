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
public class NotInProcessor extends CriteriaOperatorProcessorBase<Object> {

	@Override
	protected void executeOperation(String propriedade, Operator<Object> operator, Object... valores) {
		
		List<Object> listNotIn = Util.extrairValores(valores);
		
		if (listNotIn != null && !listNotIn.isEmpty()) {
			getJunction().add(Restrictions.not(Restrictions.in(propriedade, listNotIn)));	
		}
	}
	
}
