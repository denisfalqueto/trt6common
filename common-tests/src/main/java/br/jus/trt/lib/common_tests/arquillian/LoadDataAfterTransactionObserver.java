package br.jus.trt.lib.common_tests.arquillian;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.transaction.spi.event.AfterTransactionStarted;

import br.jus.trt.lib.common_tests.dataloader.LoadDataExecutor;

/**
 * Handler que escutará os eventos do deployer
 * 
 * @author Augusto
 *
 */
public class LoadDataAfterTransactionObserver {

	/**
	 * Observa Before event, que ocorre antes de cada método de teste.
	 * 
	 * A precendência deste evento foi configurada com o valor "-10" apenas em
	 * comparação ao valor configurado para TransactionExtension "0" e para
	 * SuiteExtension "-100". Ou seja, o carregamento dos dados deve ocorrer
	 * depois destes interceptadores. A real intensão seria executar apenas após
	 * o início da transação, no entando, o evento lançado {@link AfterTransactionStarted} não
	 * possui nenhuma informação anexado sobre o método em execução, o que me forçou a controlar
	 * a sequência através da precedência.
	 * 
	 * @param event
	 *            Before event
	 * @throws Exception
	 */
	public void afterTransactionStartedMethod(@Observes(precedence = -10) Before beforeTest) throws Exception {
		new LoadDataExecutor().process(beforeTest.getTestMethod());
	}

}
