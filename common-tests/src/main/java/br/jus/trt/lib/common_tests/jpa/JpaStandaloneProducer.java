package br.jus.trt.lib.common_tests.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.logging.log4j.Logger;

import br.jus.trt.lib.common_tests.cdi.ForTest;

/**
 * Producer para o bean {@link JPAStandalone}
 *
 * @author augusto
 *
 */
public class JpaStandaloneProducer {

    @Inject
    @ForTest
    private Logger log;

    /**
     * Mapa com as Persistence Units configuradas para execução dos testes
     * unitários. A chave no mapa é representada pelo nome da PersistenceUnit
     * (equivalente ao configurado no arquivo persistence.xml).
     */
    private static Map<String, JPAStandalone> jpas = new HashMap<String, JPAStandalone>();

    /**
     * Cria um {@link JPAStandalone} baseado no nome da persistence unit
     * definida. Esta instância criada é armazenada em um mapa para reutilização
     * durante os outros testes da mesma bateria de testes em execução.
     *
     * @return
     */
    @Produces
    @Default
    public JPAStandalone createJpaStandAlone() {
        log.entry();
        String persistenceUnitName = ConfigResolver.getProjectStageAwarePropertyValue("persistenceunit.name", "default_pu");
        log.trace("persistenceUnitName = %s", persistenceUnitName);

        JPAStandalone jpaStandalone = jpas.get(persistenceUnitName);
        if (jpaStandalone == null) {
            log.debug("Não encontrou no mapa. Criar um JpaStandalone");
            jpaStandalone = createJpaStandalone(persistenceUnitName);

            log.debug("Adicionar ao mapa");
            jpas.put(persistenceUnitName, jpaStandalone);
        }

        return log.exit(jpaStandalone);
    }

    /**
     * Factory Method para criação de um {@link JPAStandalone}.
     *
     * @param persistenceUnitName Nome da unidade de persistência (devidamente
     * configurada em um persistence.xml).
     * @return {@link JPAStandalone} devidamente configurada a partir das
     * configurações encontradas na persistence unit encontrada no arquivo
     * persistence.xml.
     */
    protected JPAStandalone createJpaStandalone(String persistenceUnitName) {
        return new JPAStandalone(persistenceUnitName);
    }

}
