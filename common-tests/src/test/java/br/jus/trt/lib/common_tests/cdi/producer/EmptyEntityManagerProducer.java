package br.jus.trt.lib.common_tests.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * Producer alternativo para injeção de dependência de um EntityManager vazio.
 * @author Augusto
 */
public class EmptyEntityManagerProducer {

	@Produces
    public EntityManager createEntityManager() {
        return null;
    }
	
}
