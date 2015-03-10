package br.jus.trt.lib.common_tests.cdi;

import java.lang.annotation.Annotation;

import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * Bean de acesso ao container do CDI em uso no ambiente de testes. 
 * @author augusto
 */
public class CDI {

   /**
    * Delegate method para operação que recupera uma instância de um determinado bean no CDI.
    * 
    * @param <T> Tipo do bean.
    * @param subtype Um {@link java.lang.Class} representando o tipo desejado.
    * @param qualifiers Qualificadores para seleção (se necessário)
    * @return A instância.
    * 
    */
   public static <T> T lookup(Class<T> subtype, Annotation... qualifiers) {
	   return BeanProvider.getContextualReference(subtype, qualifiers);
   }
   
   /**
    * Delegate method para operação que recupera uma instância de um determinado bean no CDI.
    * 
    * @param <T> Tipo do bean.
    * @param subtype Um {@link java.lang.Class} representando o tipo desejado.
    * @param qualifiers Qualificadores para seleção (se necessário)
    * @return A instância.
    * 
    */
   public static <T> T lookup(Class<T> subtype) {
	   return BeanProvider.getContextualReference(subtype);
   }
    
}
