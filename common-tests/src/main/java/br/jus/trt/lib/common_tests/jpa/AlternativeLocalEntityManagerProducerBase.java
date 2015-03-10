package br.jus.trt.lib.common_tests.jpa;

import javax.persistence.EntityManager;

import br.jus.trt.lib.common_tests.LocalTransactionTestBase;

/**
 * Producer alternativo para injeção de dependência de um EntityManager em ambiente de testes.
 * O EntityManager utilizado é gerenciado pelo {@link JPAStandalone}, e geralmente é compartilhado
 * por outros recursos da estrutura de testes local, inclusive o gerenciamento local de transações
 * em {@link LocalTransactionTestBase}.
 * <b>Para ser especializado em cada módulo que o utilizar</b>.
 * @author Augusto
 */
public abstract class AlternativeLocalEntityManagerProducerBase {

	/**
	 * Este método deverá ser implementado no Producer concreto porque o CDI prece que não
	 * lê a anotação @Producer em uma superclasse.
	 *  <br/>
	 *  Implementação sugerida:
	 *  <br/>
	 *  <code>
	 *  	\@Produces
	 *  	public EntityManager produceEntityManager(JPAStandalone jpaStandalone) {
	 *  		return createEntityManager(jpaStandalone);
	 *  	}
	 *  </code>
	 *  
	 *  
	 *  
	 * @param jpaStandalone Container que gerencia o EntityManager.
	 * @return EntityManager a ser injetado.
	 */
	public abstract EntityManager produceEntityManager(JPAStandalone jpaStandalone);
	
	/**
	 * Retorna uma instancia de {@link EntityManager} baseado no {@link JPAStandalone} default informado.
	 * @param jpaStandalone Instância em utilização pelo teste unitário.
	 * @return {@link EntityManager} devidamente configurado e pronto para uso.
	 */
    protected EntityManager createEntityManager(JPAStandalone jpaStandalone) {
		jpaStandalone.startSession(); // garante um EM disponível
        return jpaStandalone.getEm();
    }
	
}
