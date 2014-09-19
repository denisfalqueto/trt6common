package br.jus.trt.lib.qbe.repository.criteria.operator;

import org.hibernate.criterion.MatchMode;

import br.jus.trt.lib.qbe.api.operator.Like;
import br.jus.trt.lib.qbe.api.operator.LikeSufix;

/**
 * Processador do operador {@link LikeSufix} 
 * @author augusto
 */
@SuppressWarnings("serial")
public class LikeSufixProcessor extends LikeProcessor {

	/**
	 * Define o tipo de like que será executado
	 * @param like Operador sendo processado.
	 * @return 
	 */
	protected MatchMode getMatchMode(Like like) {
		return MatchMode.END;
	}
	
}
