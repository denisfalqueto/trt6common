package br.jus.trt.lib.common_tests.arquillian;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Producer para EntityManager, de forma possam ser injetadas em beans CDI
 * convencionais, sem a necessidade da anotação @PersistenceUnit. Basta um
 * simples @Inject. 
 */
@Alternative
public class AlternativeEntityManagerProducer {
    @PersistenceUnit
    private EntityManagerFactory emf;
    
    @Produces 
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }
    
    public void closeEntityManager(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }
}
