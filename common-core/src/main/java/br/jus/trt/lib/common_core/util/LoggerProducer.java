package br.jus.trt.lib.common_core.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsável para criação um Logger para injeção via CDI.
 */
public class LoggerProducer {

    private Logger log = LogManager.getLogger();

    private Class<?> getInjectionClass(InjectionPoint ip) {
        log.entry(ip);
        Class<?> result = null;
        if (ip.getBean() != null) {
            result = ip.getBean().getBeanClass();
        }
        return log.exit(result);
    }

    @Produces
    public Logger createLog4j2Logger(InjectionPoint caller) {
        return LogManager.getFormatterLogger(getInjectionClass(caller));
    }
}
