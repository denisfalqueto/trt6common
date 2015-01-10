package br.jus.trt.lib.common_tests;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import br.jus.trt.lib.common_tests.jpa.JPAStandalone;
import br.jus.trt.lib.common_tests.util.QuerierUtil;

/**
 * Classe base com comportamento comum para testes unitários que utilizam um
 * contexto transacional. Gerencia o ciclo de vida do JUnit, JPA, suporta CDI, e
 * outros recursos necessários para testes.
 * 
 * Cada método de teste é isolado em uma transação, não interferindo um no outro. Ao final de cada método,
 * é realizado rollback na transação.
 * 
 * @author Augusto
 * 
 */
@Ignore
public class LocalTransactionTestBase extends TestBase {

	/** para controle de transações e acesso à base de dados */
	private JPAStandalone jpaInstance;

	@Inject
	private QuerierUtil querier;
	
	/**
	 * Executado antes de cada método de teste. Realiza o controle do início da
	 * transação.
	 */
	@Before
	public void beforeEachTest() {
		jpaInstance.startSession();
		jpaInstance.startTransaction();
	}

	/**
	 * Executado após de cada método de teste. Realiza o controle do término da
	 * transação.
	 */
	@After
	public void afterEachTest() {
		jpaInstance.rollbackTransaction();
		jpaInstance.closeSession();
	}

	public EntityManager getEntityManager() {
		return jpaInstance.getEm();
	}

	public JPAStandalone getJpa() {
		return jpaInstance;
	}

	@Inject
	public void setJpa(JPAStandalone jpa) {
		this.jpaInstance = jpa;
	}

	public QuerierUtil getQuerier() {
		return querier;
	}

	public void setQuerier(QuerierUtil querier) {
		this.querier = querier;
	}
}
