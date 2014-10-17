package br.jus.trt.lib.common_tests.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import br.jus.trt.lib.common_tests.cdi.ForTest;

/**
 * Producer de um {@link Logger} para utilização em ambiente de testes.
 *
 * Serão produzidos vários tipos de loggers, mas todos são direcionados para o
 * Log4j2.
 *
 * @author augusto
 *
 */
public class LoggerProducer {

    private Class<?> getInjectionClass(InjectionPoint ip) {
        return ip.getBean().getClass();
    }

    @Produces
    @ForTest
    protected org.apache.logging.log4j.Logger createLog4j2Logger(InjectionPoint caller) {
        return org.apache.logging.log4j.LogManager.getLogger(getInjectionClass(caller));
    }

    @Produces
    @ForTest
    protected org.apache.log4j.Logger createLog4jLogger(InjectionPoint caller) {
        return org.apache.log4j.Logger.getLogger(getInjectionClass(caller));
    }
    
    @Produces
    @ForTest
    protected org.slf4j.Logger createSlf4jLogger(InjectionPoint caller) {
        return org.slf4j.LoggerFactory.getLogger(getInjectionClass(caller));
    }

    @Produces
    protected org.apache.commons.logging.Log createCommonsLogger(InjectionPoint caller) {
        return org.apache.commons.logging.LogFactory.getLog(getInjectionClass(caller));
    }

}
