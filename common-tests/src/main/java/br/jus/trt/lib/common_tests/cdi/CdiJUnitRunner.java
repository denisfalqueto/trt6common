package br.jus.trt.lib.common_tests.cdi;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class CdiJUnitRunner extends BlockJUnit4ClassRunner {

    private final Class<?> clazz;
    public CdiJUnitRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
    }
 
    @Override
    protected Object createTest() throws Exception {
		final Object test = CDI.getInstance().instance(clazz);
        return test;
    }

}
