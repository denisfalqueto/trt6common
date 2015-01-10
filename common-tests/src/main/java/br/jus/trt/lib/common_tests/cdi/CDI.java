package br.jus.trt.lib.common_tests.cdi;

import java.lang.annotation.Annotation;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Bean de acesso ao container do CDI em uso no ambiente de testes. 
 * @author augusto
 */
public class CDI {

    private Weld weld;
    private WeldContainer container;
    
    private static CDI instance;
    
    protected CDI() {
        this.weld = new Weld();
        this.container = weld.initialize();
    }
    
    /**
     * @return Singleton.
     */
    public static CDI getInstance() {
    	if (instance == null) {
    		instance = new CDI();
    	}
    	return instance;
    }

	public Weld getWeld() {
		return weld;
	}

	/**
	 * @return Container de injeção de dependências do CDI.
	 */
	public WeldContainer getContainer() {
		return container;
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
   public <T> T lookup(Class<T> subtype, Annotation... qualifiers) {
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
   public <T> T lookup(Class<T> subtype) {
	   return BeanProvider.getContextualReference(subtype);
   }
    
}
