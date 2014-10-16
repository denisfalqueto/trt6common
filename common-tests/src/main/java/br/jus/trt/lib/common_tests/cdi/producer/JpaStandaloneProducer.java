package br.jus.trt.lib.common_tests.cdi.producer;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import br.jus.trt.lib.common_tests.jpa.JPAStandalone;

/**
 * Producer para o bean {@link JPAStandalone}
 * @author augusto
 *
 */
public class JpaStandaloneProducer {

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
    public JPAStandalone createJpaStandAlone() {
		String persistenceUnitName = "default_pu"; // FIXME deixar este nome dinâmico de alguma forma.
		
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
	 * Factory Method para criação de um {@link JPAStandalone}.
	 * @param persistenceUnitName Nome da unidade de persistência (devidamente configurada em um persistence.xml).
	 * @return {@link JPAStandalone} devidamente configurada a partir das configurações encontradas na
	 * persistence unit encontrada no arquivo persistence.xml.
	 */
	protected JPAStandalone createJpaStandalone(String persistenceUnitName) {
		return new JPAStandalone(persistenceUnitName);
	}

}
