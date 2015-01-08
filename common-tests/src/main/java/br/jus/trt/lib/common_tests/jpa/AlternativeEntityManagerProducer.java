package br.jus.trt.lib.common_tests.jpa;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * Producer alternativo para injeção de dependência de um EntityManager em ambiente de testes.
 * @author Augusto
 */
@Alternative
public class AlternativeEntityManagerProducer {

	/**
	 * Retorna uma instancia de {@link EntityManager} baseado no {@link JPAStandalone} default informado.
	 * @param jpaStandalone Instância em utilização pelo teste unitário.
	 * @return {@link EntityManager} devidamente configurado e pronto para uso.
	 */
	@Produces @Alternative
    public EntityManager createEntityManager(JPAStandalone jpaStandalone) {
		jpaStandalone.startSession(); // garante um EM disponível
        return jpaStandalone.getEm();
    }
	
}
