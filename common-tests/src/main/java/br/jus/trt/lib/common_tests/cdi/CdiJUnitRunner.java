package br.jus.trt.lib.common_tests.cdi;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class CdiJUnitRunner extends BlockJUnit4ClassRunner {

    private final Class<?> clazz;
    private final Weld weld;
    private final WeldContainer container;
 
    public CdiJUnitRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
        this.weld = new Weld();
        this.container = weld.initialize();
    }
 
    @Override
    protected Object createTest() throws Exception {
		final Object test = container.instance().select(clazz).get();
        return test;
    }

}
