package br.jus.trt.lib.common_tests.cdi.producer;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import br.jus.trt.lib.common_tests.cdi.ForTest;

/**
 * Producer de um {@link Logger} para utilização em ambiente de testes.
 * @author augusto
 *
 */
public class LoggerProducer {

	@Produces @ForTest
	protected Logger createLogger(InjectionPoint caller) {
		return Logger.getLogger(caller.getBean().getClass().getName());
	}

}
