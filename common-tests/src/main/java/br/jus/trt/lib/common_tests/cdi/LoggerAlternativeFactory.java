package br.jus.trt.lib.common_tests.cdi;

import java.util.logging.Logger;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@Alternative
public class LoggerAlternativeFactory {

	@Produces
	protected Logger createLogger(InjectionPoint caller) {
		return Logger.getLogger(caller.getBean().getClass().getName());
	}

}
