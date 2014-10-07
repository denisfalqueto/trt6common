package br.jus.trt.lib.common_tests.cdi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;

import br.jus.trt.lib.common_tests.jpa.JPAStandalone;

/**
 * Contém CDI Producers alternativos para utilização em ambiente de testes.
 * @author Augusto
 */
@Alternative
public class AlternativeProducers {

	private static Map<String, JPAStandalone> jpas = new HashMap<String, JPAStandalone>();
	
	@Produces 
    public JPAStandalone produceJpaStandAlone() {
		String persistenceUnitName = "default_pu";
		
		JPAStandalone jpaStandalone;
		if (jpas.containsKey(persistenceUnitName)) {
			jpaStandalone = jpas.get(persistenceUnitName);
		} else {
			jpaStandalone = createJpaStandalone(persistenceUnitName);
			jpas.put(persistenceUnitName, jpaStandalone);
		}
		
        return jpaStandalone;
    }
	
	@Produces 
    public EntityManager produceEntityManager(JPAStandalone jpaStandalone) {
        return jpaStandalone.getEm();
    }
	
	protected JPAStandalone createJpaStandalone(String persistenceUnitName) {
		return new JPAStandalone(persistenceUnitName);
	}

	@Produces
	public Logger createLogger(InjectionPoint caller) {
		return Logger.getLogger(caller.getBean().getClass().getName());
	}
	
}
