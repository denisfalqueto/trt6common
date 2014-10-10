package br.jus.trt.lib.common_tests.dataloader;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.CDI;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.junit.Test;

/**
 * Interceptador associado à anotação {@link LoadData} e executa as tarefas de
 * interceptação das classes de testes e execução dos DataLoaders configurados.
 */

@SuppressWarnings("serial")
@LoadData
@Interceptor
public class LoadDataInterceptor implements Serializable {

	/**
	 * Método interceptador que envolve a chamada ao componente
	 */
	@AroundInvoke
	public Object aroundInvoke(InvocationContext invocation) throws Exception {
		
		if (mustLoadData(invocation.getMethod())) {
			Collection<DataLoaderPrecedence> dataLoaders =  extractDataLoaders(invocation.getMethod());
			
			// executando sequencialmente para garantir a precedência
			for (DataLoader dataLoader : dataLoaders) {
				dataLoader.load();
			}
		}
		
		return invocation.proceed();
	}

	/**
	 * Extraíndo as informações de {@link DataLoader} configurados através das anotações 
	 * {@link LoadDatas}, {@link LoadData}.
	 * @param method Método que está sendo interceptado.
	 * @return Coleção de {@link DataLoader}, devidamente ordenada (decrescente) segundo as precedências configuradas .
	 */
	protected Collection<DataLoaderPrecedence> extractDataLoaders(Method method) {
		
		// carregando os dataLoaders configurados diretamente
		List<DataLoaderPrecedence> loaders = new ArrayList<DataLoaderPrecedence>();

		// carrega as configurações de loaders da classe
		extractLoadDatas(method.getDeclaringClass(), loaders);
		extractLoadData(method.getDeclaringClass(), loaders);
		
		// carrega as configurações de loaders do método
		extractLoadDatas(method, loaders);
		extractLoadData(method, loaders);
		
		Collections.sort(loaders);
		
		return loaders;
	}

	/**
	 * Cria um ou mais {@link DataLoader} a partir de informações extraídas 
	 * de anotação do tipo {@link LoadData} presente. 
	 */
	private void extractLoadData(AnnotatedElement element, List<DataLoaderPrecedence> loaders) {
		LoadData loadData = element.getAnnotation(LoadData.class);
		if(loadData != null) {
			addDataLoader(loaders, loadData);
		}
	}
	
	/**
	 * Cria um ou mais {@link DataLoader} a partir de informações extraídas 
	 * de anotação do tipo {@link LoadDatas} presente. 
	 */
	private void extractLoadDatas(AnnotatedElement element, List<DataLoaderPrecedence> loaders) {
		LoadDatas loadDatas = element.getAnnotation(LoadDatas.class);
		if (loadDatas != null) {
			for (LoadData loadData : loadDatas.value()) {
				addDataLoader(loaders, loadData);
			}
		}
	}

	/**
	 * Cria um ou mais {@link DataLoader} a partir de um {@link LoadData}.  
	 */
	private void addDataLoader(Collection<DataLoaderPrecedence> loaders, LoadData loadData) {
		
		// carregando scripts
		String[] scripts = loadData.sql();
		for (String script : scripts) {
			DataLoaderSQL scriptDataLoader = CDI.current().select(DataLoaderSQL.class).get();
			scriptDataLoader.setScript(script);
			
			DataLoaderPrecedence dataLoader = new DataLoaderPrecedence(loadData.precedence(), scriptDataLoader);
			loaders.add(dataLoader);
		}
		
		// carregando beans
		Class<? extends DataLoader>[] beans = loadData.bean();
		for (Class<? extends DataLoader> dataLoaderType : beans) {
			DataLoader beanDataLoader = CDI.current().select(dataLoaderType).get();
			
			DataLoaderPrecedence dataLoader = new DataLoaderPrecedence(loadData.precedence(), beanDataLoader);
			loaders.add(dataLoader);
		}
	}
	
	/**
	 * Quando o interceptador está colocado sobre uma classe todos os métodos são interceptados. Nestes casos,
	 * apenas o métodos de teste devem disparar o carregamento de dados (quando há a presença da anotação @{@link Test} . 
	 * Por outro lado, se o interceptador é colocado diretamente sobre um método, este deve provocar o carregamente 
	 * de dados sempre.
	 * @param method Método que está sendo interceptado.
	 * @return true caso o método está dentro das condições para disparar o carregamento de dados.
	 */
	protected boolean mustLoadData(Method method) {
		/*
		 * É necessário apenas verificar a existência da anotação no método diretamente, visto que,
		 * já que o interceptador foi invocado, no mínimo a classe já foi anotada.
		 */
		return method.isAnnotationPresent(LoadData.class)
				|| method.isAnnotationPresent(LoadDatas.class)	
				|| method.isAnnotationPresent(Test.class); 
	}

	/**
	 * Wrapper para devida ordenação dos {@link DataLoader}s
	 */
	private class DataLoaderPrecedence implements DataLoader, Comparable<DataLoaderPrecedence> {
		
		private int precendence;
		private DataLoader dataLoader;
		
		public DataLoaderPrecedence(int precendence, DataLoader dataLoader) {
			super();
			this.precendence = precendence;
			this.dataLoader = dataLoader;
		}
		
		public int getPrecendence() {
			return precendence;
		}
		public DataLoader getDataLoader() {
			return dataLoader;
		}

		/**
		 * Compara a sequencia de acordo com a precedência configurada.
		 * A ordenação se dará de forma decrescente, sendo o objeto com maior precedência no início.
		 */
		@Override
		public int compareTo(DataLoaderPrecedence o) {
			return o.getPrecendence() - this.getPrecendence();
		}

		@Override
		public void load() throws Exception {
			getDataLoader().load();
		}
		
		@Override
		public String toString() {
			return precendence + " - " + dataLoader;
		}
		
	}
}