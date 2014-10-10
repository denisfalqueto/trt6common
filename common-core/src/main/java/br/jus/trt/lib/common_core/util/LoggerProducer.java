package br.jus.trt.lib.common_core.util;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Responsável para criação um Logger para injeção via CDI.
 */
public class LoggerProducer {
   @Produces
   public Logger produceLog(InjectionPoint injectionPoint) {
      return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
   }
}
