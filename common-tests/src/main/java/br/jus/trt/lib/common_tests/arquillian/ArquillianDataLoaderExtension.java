package br.jus.trt.lib.common_tests.arquillian;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.transaction.spi.event.TransactionEvent;
import org.reflections.Reflections;

import br.jus.trt.lib.common_tests.dataloader.LoadData;
import br.jus.trt.lib.common_tests.dataloader.LoadDatas;

/**
 * Arquillian Extension para permitir a integração dos componentes de
 * {@link LoadData} e {@link LoadDatas} nas classes de testes que usam o
 * Arquillian como Runner.
 * 
 * Esta Extension é ativada através do arquivo
 * META-INF/services/org.jboss.arquillian.core.spi.LoadableExtension, segundo as
 * definições de configuração do Arquillian SPI.
 * 
 * @author Augusto
 *
 */
public class ArquillianDataLoaderExtension implements RemoteLoadableExtension {

	@Inject
	private Event<TransactionEvent> lifecycleEvent;

	/**
	 * Classes que serão observadas para serem interceptadas.
	 */
	Set<Class<?>> classesToObserve = new HashSet<Class<?>>();

	@Override
	public void register(ExtensionBuilder builder) {
		loadDataLoadAnnotated();
		builder.observer(LoadDataAfterTransactionObserver.class);
	}

	private void loadDataLoadAnnotated() {
		Reflections reflections = new Reflections("");
		loadLoadData(reflections, LoadData.class);
		loadLoadData(reflections, LoadDatas.class);
	}

	private void loadLoadData(Reflections reflections,
			Class<? extends Annotation> annotation) {
		// carregando as classes diretamente anotadas
		Set<Class<?>> loadDataAnnotatedClasses = reflections
				.getTypesAnnotatedWith(annotation, true);
		if (loadDataAnnotatedClasses != null) {
			classesToObserve.addAll(loadDataAnnotatedClasses);
		}

		// carregando as classes com métodos anotados.
		Set<Method> loadDataAnnotatedMethods = reflections
				.getMethodsAnnotatedWith(annotation);
		if (loadDataAnnotatedMethods != null) {
			for (Method method : loadDataAnnotatedMethods) {
				classesToObserve.add(method.getDeclaringClass());
			}
		}
	}

}
