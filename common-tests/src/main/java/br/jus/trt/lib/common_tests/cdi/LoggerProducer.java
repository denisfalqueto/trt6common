package br.jus.trt.lib.common_tests.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        return ip.getMember().getDeclaringClass();
    }

    @Produces
    @ForTest
    public Logger createLog4j2Logger(InjectionPoint caller) {
		Class<?> injectionClass = getInjectionClass(caller);
		Logger logger = LogManager.getFormatterLogger(injectionClass);
   		return logger;
    }

}
