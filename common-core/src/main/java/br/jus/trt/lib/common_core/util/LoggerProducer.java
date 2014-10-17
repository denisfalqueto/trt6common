package br.jus.trt.lib.common_core.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Responsável para criação um Logger para injeção via CDI.
 */
public class LoggerProducer {

    private Class<?> getInjectionClass(InjectionPoint ip) {
        return ip.getBean().getClass();
    }

    @Produces
    protected org.apache.logging.log4j.Logger createLog4j2Logger(InjectionPoint caller) {
        return org.apache.logging.log4j.LogManager.getLogger(getInjectionClass(caller));
    }

    @Produces
    protected org.apache.log4j.Logger createLog4jLogger(InjectionPoint caller) {
        return org.apache.log4j.Logger.getLogger(getInjectionClass(caller));
    }

    @Produces
    protected org.slf4j.Logger createSlf4jLogger(InjectionPoint caller) {
        return org.slf4j.LoggerFactory.getLogger(getInjectionClass(caller));
    }

    @Produces
    protected org.apache.commons.logging.Log createCommonsLogger(InjectionPoint caller) {
        return org.apache.commons.logging.LogFactory.getLog(getInjectionClass(caller));
    }

}
