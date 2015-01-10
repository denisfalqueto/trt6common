package br.jus.trt.lib.common_tests.arquillian;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.reflections.Reflections;

import br.jus.trt.lib.common_tests.dataloader.LoadData;
import br.jus.trt.lib.common_tests.dataloader.LoadDataExecutor;
import br.jus.trt.lib.common_tests.dataloader.LoadDatas;

/**
 * Arquillian Extension para permitir a integração dos componentes de {@link LoadData}
 * e {@link LoadDatas} nas classes de testes que usam o Arquillian como Runner.
 * 
 * Esta Extension é ativada através do arquivo META-INF/services/org.jboss.arquillian.core.spi.LoadableExtension, 
 * segundo as definições de configuração do Arquillian SPI.
 * @author Augusto
 *
 */
public class ArquillianDataLoaderExtension implements LoadableExtension {

	/**
	 * Classes que serão observadas para serem interceptadas.
	 */
	Set<Class<?>> classesToObserve = new HashSet<Class<?>>(); 
	
	@Override
	public void register(ExtensionBuilder builder) {
		loadDataLoadAnnotated();
		if (!classesToObserve.isEmpty()) {
			builder.observer(LoadDataDeployer.class);
		}
	}

	private void loadDataLoadAnnotated() {
		Reflections reflections = new Reflections("");
		loadLoadData(reflections, LoadData.class);
		loadLoadData(reflections, LoadDatas.class);
	}

	private void loadLoadData(Reflections reflections, Class<? extends Annotation> annotation) {
		// carregando as classes diretamente anotadas
		Set<Class<?>> loadDataAnnotatedClasses = reflections.getTypesAnnotatedWith(annotation, true);
		if (loadDataAnnotatedClasses != null) {
			classesToObserve.addAll(loadDataAnnotatedClasses);
		}
		
		// carregando as classes com métodos anotados.
		Set<Method> loadDataAnnotatedMethods = reflections.getMethodsAnnotatedWith(annotation);
		if (loadDataAnnotatedMethods != null) {
			for (Method method : loadDataAnnotatedMethods) {
				classesToObserve.add(method.getDeclaringClass());
			}
		}
	}
	
	/**
	 * Handler que escutará os eventos do deployer
	 * @author Augusto
	 *
	 */
	public static class LoadDataDeployer {
		
        /**
         * Observa Before event, que ocorre antes de cada método de teste.
    	 * 
    	 * A precendência deste evento foi configurada com o valor "-10" apenas em comparação ao valor
    	 * configurado para TransactionExtension "0" e para SuiteExtension "-100". Ou seja, o carregamento dos
    	 * dados deve ocorrer depois destes interceptadores.
    	 * 
         * @param event Before event
         * @throws Exception 
         */
        public void beforeTestMethod(@Observes(precedence = -1000) final Before event) throws Exception {
//        	LoadDataExecutor executor = BeanProvider.getContextualReference(LoadDataExecutor.class);
        	new LoadDataExecutor().process(event.getTestMethod());
        }		
		
	}
}
