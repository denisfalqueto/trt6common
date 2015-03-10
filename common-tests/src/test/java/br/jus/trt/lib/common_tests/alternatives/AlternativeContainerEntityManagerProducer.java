package br.jus.trt.lib.common_tests.alternatives;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Producer para EntityManager, de forma que possam ser injetadas em beans CDI
 * convencionais, sem a necessidade da anotação @PersistenceUnit. Basta um
 * simples @Inject. 
 */
@Alternative

/*
 * Ao configurar a Proriedade abaixo, este Alternative passa a ser visível fora de módulo do CDI (test), podendo
 * ser injetado em instâncias do módulo main.
 */
@Priority(1)
public class AlternativeContainerEntityManagerProducer {
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
