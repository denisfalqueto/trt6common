package br.jus.trt.lib.common_tests.arquillian;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.transaction.spi.event.TransactionEvent;

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

	@Override
	public void register(ExtensionBuilder builder) {
		builder.observer(LoadDataAfterTransactionObserver.class);
	}

}
