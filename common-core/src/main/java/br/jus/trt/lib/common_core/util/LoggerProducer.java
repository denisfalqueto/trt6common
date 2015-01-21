package br.jus.trt.lib.common_core.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsável para criação um Logger para injeção via CDI.
 */
public class LoggerProducer {

	private Class<?> getInjectionClass(InjectionPoint ip) {
		return ip.getMember().getDeclaringClass();
	}

	@Produces
	public Logger createLog4j2Logger(InjectionPoint caller) {
		return LogManager.getFormatterLogger(getInjectionClass(caller));
	}

}
