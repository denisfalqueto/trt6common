package br.jus.trt.lib.common_core.integration;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Producer para EntityManager, de forma possam ser injetadas em beans CDI
 * convencionais, sem a necessidade da anotação @PersistenceUnit. Basta um
 * simples @Inject. Isto é um requisito do módulo JPA e Data do DeltaSpike,
 * mas tem utilidade tambeḿ fora desses módulos.
 * @author denisf
 */
public class EntityManagerProducer {
	
	@Inject
    private Logger log;
    
    @PersistenceUnit
    private EntityManagerFactory emf;
    
    @Produces 
    public EntityManager produceEntityManager() {
        log.entry();
        return log.exit(emf.createEntityManager());
    }
    
    public void closeEntityManager(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }
}
