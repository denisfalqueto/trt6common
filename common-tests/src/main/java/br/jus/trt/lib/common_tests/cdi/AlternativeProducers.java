package br.jus.trt.lib.common_tests.cdi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
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

	/**
	 * Mapa com as Persistence Units configuradas para execução dos testes unitários.
	 * A chave no mapa é representada pelo nome da PersistenceUnit (equivalente ao configurado no
	 * arquivo persistence.xml).
	 */
	private static Map<String, JPAStandalone> jpas = new HashMap<String, JPAStandalone>();
	
	/**
	 * Cria um {@link JPAStandalone} baseado no nome da persistence unit definida. Esta instância
	 * criada é armazenada em um mapa para reutilização durante os outros testes da mesma bateria
	 * de testes em execução.
	 * @return
	 */
	@Produces @Default
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
	
	/**
	 * Retorna uma instancia de {@link EntityManager} baseado no {@link JPAStandalone} default informado.
	 * @param jpaStandalone Instância em utilização pelo teste unitário.
	 * @return {@link EntityManager} devidamente configurado e pronto para uso.
	 */
	@Produces 
    public EntityManager produceEntityManager(JPAStandalone jpaStandalone) {
        return jpaStandalone.getEm();
    }
	
	/**
	 * Factory Method para criação de um {@link JPAStandalone}.
	 * @param persistenceUnitName Nome da unidade de persistência (devidamente configurada em um persistence.xml).
	 * @return {@link JPAStandalone} devidamente configurada a partir das configurações encontradas na
	 * persistence unit encontrada no arquivo persistence.xml.
	 */
	protected JPAStandalone createJpaStandalone(String persistenceUnitName) {
		return new JPAStandalone(persistenceUnitName);
	}

	/**
	 * Cria um Logger básico para utilização em testes unitários.
	 * @param caller para obtenção dos dados do bean interceptado.
	 * @return Logger configurado segundo o bean interceptado.
	 */
	@Produces
	public Logger createLogger(InjectionPoint caller) {
		return Logger.getLogger(caller.getBean().getClass().getName());
	}
	
}
