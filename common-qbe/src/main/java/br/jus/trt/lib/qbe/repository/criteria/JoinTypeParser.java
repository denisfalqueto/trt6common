package br.jus.trt.lib.qbe.repository.criteria;

import br.jus.trt.lib.qbe.api.JoinType;

/**
 * Classe auxiliar que sabe transforma um {@link JoinType} em um {@link org.hibernate.sql.JoinType} 
 * @author augusto
 */
public class JoinTypeParser {

	/**
	 * @param joinType QBE JoinType.
	 * @return JoinType equivalente em Criteria.
	 */
	public org.hibernate.sql.JoinType parse(JoinType joinType) {
		
		org.hibernate.sql.JoinType criteriaJoin = null;
		
		switch (joinType) {
		case LEFT:
			criteriaJoin = org.hibernate.sql.JoinType.LEFT_OUTER_JOIN;
			break;
		case INNER:
			criteriaJoin = org.hibernate.sql.JoinType.INNER_JOIN;
			break;			
		default:
			// left como default
			criteriaJoin = org.hibernate.sql.JoinType.LEFT_OUTER_JOIN;
			break;
		}
		
		return criteriaJoin;
	}
}
