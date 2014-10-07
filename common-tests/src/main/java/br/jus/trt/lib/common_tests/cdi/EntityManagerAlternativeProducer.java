package br.jus.trt.lib.common_tests.cdi;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import br.jus.trt.lib.common_tests.jpa.JPAStandalone;

/**
 * Producer para EntityManager, de forma possam ser injetadas em beans CDI
 * convencionais, sem a necessidade da anotação @PersistenceUnit. Basta um
 * simples @Inject. Isto é um requisito do módulo JPA e Data do DeltaSpike,
 * mas tem utilidade tambeḿ fora desses módulos.
 * @author denisf
 */
public class EntityManagerAlternativeProducer {

	@Produces @Alternative
    public EntityManager produceEntityManager(JPAStandalone jpaStandalone) {
        return jpaStandalone.getEm();
    }
    
}
