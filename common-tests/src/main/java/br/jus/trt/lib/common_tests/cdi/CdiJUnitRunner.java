package br.jus.trt.lib.common_tests.cdi;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Junit Runner que inicializa um container CDI (Weld) para permitir a execução de testes
 * com os recursos de Injeção de Dependência.
 * @author Augusto
 *
 */
public class CdiJUnitRunner extends BlockJUnit4ClassRunner {

    private final Class<?> clazz;
    private Weld weld;
    private WeldContainer container;
    
    public CdiJUnitRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
        this.weld = new Weld();
        this.container = weld.initialize();
    }
 
    @Override
    protected Object createTest() throws Exception {
    	/*
    	 * O objeto de teste é criado junto ao container CDI, assim já será possível
    	 * utilizar a injeção de dependência dentro da própria classe de teste.
    	 */
		final Object test = container.instance().select(clazz).get();
        return test;
    }

}
