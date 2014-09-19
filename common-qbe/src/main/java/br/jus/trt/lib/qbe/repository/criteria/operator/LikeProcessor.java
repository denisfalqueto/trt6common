package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.api.operator.Like;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaOperatorProcessorBase;

/**
 * Processador do operador {@link Between} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class LikeProcessor extends CriteriaOperatorProcessorBase<String> {

	@Override
	protected void executeOperation(String property, Operator<String> operator, String... values) {
		
		Like like = (Like) operator;
		String value = values[0];
		if (like.isCaseSensitive()) {
			getJunction().add(Restrictions.like(property, value, getMatchMode(like) ));
		} else {
			getJunction().add(Restrictions.ilike(property, value.toLowerCase(), getMatchMode(like) ));					
		}
	}

	/**
	 * Define o tipo de like que será executado
	 * @param like Operador sendo processado.
	 * @return 
	 */
	protected MatchMode getMatchMode(Like like) {
		return MatchMode.ANYWHERE;
	}
	
}
