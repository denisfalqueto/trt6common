package br.jus.trt.lib.common_tests.dataloader;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;


/**
 * Interceptador associado à anotação {@link LoadData} e executa as tarefas de
 * interceptação das classes de testes e execução dos DataLoaders configurados.
 */

@SuppressWarnings("serial")
@LoadDatas
@Interceptor
public class LoadDatasInterceptor extends LoadInterceptor {

	@Override
	@AroundInvoke
	public Object aroundInvoke(InvocationContext invocation) throws Exception {
		// redefinindo método apenas para adicionar a anotacao @AroundInvoke
		return super.aroundInvoke(invocation);
	}
	
}