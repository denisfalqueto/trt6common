package br.jus.trt.lib.common_tests.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

/**
 * Classe utilitária que permite a utilização e gerenciamento de transações e
 * conexões com a base de dados de forma StandAlone, complementamente independente.
 * <br/>
 * Consegue ler um arquivo "persistence.xml", configurar um EntityManagerFactory e disponibilizar
 * um EntityManager para uso desacoplado de qualquer contexto. 
 * <br/>
 * Não é uma classe estática, e manipula apenas uma PersistenceUnit por vez. Para situações onde
 * são necessárias utilizar mais de uma base de dados, deve-se intanciar vários objetos desta classe.
 */
public class JPAStandalone {
	
	/** Factory associada à persistence unit */
	private EntityManagerFactory emf;
	
	/** EntityManager utilizado nas operações */
	private EntityManager em;

	/** Nome da persistence unit configurado no arquivo percistence.xml*/
	private String persistenceUnitName; 
	
	/**
	 * @param persistenceUnitName Nome da Persistence unit
	 */
	public JPAStandalone(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
		configure();
	}
	
	/**
	 * @param emf {@link EntityManagerFactory} para associar a esta classe.
	 */
	public JPAStandalone(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	/**
	 * Inicializa uma EntityManagerFactory
	 */
	private void configure() {
		emf = Persistence.createEntityManagerFactory(persistenceUnitName);
	}

	/**
	 * Inicia um novo EntityManager caso não exista um já pronto para uso.
	 */
	public void startSession() {
		if (em == null || !em.isOpen()) {
			em = emf.createEntityManager();
		}	
	}
	
	/**
	 * Inicia um bloco transacional para operações na base de dados
	 */
	public void startTransaction() {
		// iniciando uma transação
		em.getTransaction().begin();
	}
	
	/**
	 * Realiza commit e encerra a transação.
	 */
	public void commitTransaction() {
		if (em.getTransaction().isActive()) {
			em.getTransaction().commit();
		}	
	}
	
	/**
	 * Realiza rollback e encerra a transação
	 */
	public void rollbackTransaction() {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}	
	}

	public void closeSession() {
		em.close();
	}
	
	public void clearCache() {
		em.clear();
	}
	
	/**
	 * Persiste um objeto mapeado na base de ddos
	 * @param entidade Objeto mapeado.
	 */
	public void save(Object entidade) {
		em.persist(entidade);
		em.flush();
	}
	
	/**
	 * Exclui um objeto mapeado da base de dados
	 * @param entidade Objeto mapeado
	 */
	public void remove(Object entidade) {
		em.remove(entidade);
		em.flush();
	}
	
	/**
	 * Atualiza um objeto mapeado na base de dados
	 * @param entidade Objeto mapeado
	 */
	public Object update(Object entidade) {
		Object merge = em.merge(entidade);
		em.flush();
		return merge;
	}
	
	/**
	 * Consulta um objeto mapeado através da chave primária
	 * @param <TIPO> Tipo do objeto
	 * @param classEntidade Classe do objeto mapeado
	 * @param chave Chave primária para identificação do objeto
	 * @return Objeto com as informações recuperadas da base de dados
	 */
	public <TIPO> TIPO find(Class<TIPO> classEntidade, Object chave) {
		return em.find(classEntidade, chave);
	}
	
	/**
	 * Lista todos os objetos persistidos de um determinado tipo
	 * @param <TIPO> Tipo do objeto
	 * @param classeEntidade Classo do objeto mapeado
	 * @return todos os objetos persistidos
	 */
	@SuppressWarnings("unchecked")
	public <TIPO> List<TIPO> list(Class<TIPO> classeEntidade) {
		return em.createQuery("from " + classeEntidade.getSimpleName()).getResultList();
	}

	public EntityManager getEm() {
		return em;
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}
	
	public Session getSession() {
		return  getEm() != null ? (Session) getEm().getDelegate() : null;
	}
}
