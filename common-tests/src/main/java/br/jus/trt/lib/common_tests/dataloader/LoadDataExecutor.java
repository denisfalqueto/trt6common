package br.jus.trt.lib.common_tests.dataloader;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.jus.trt.lib.common_tests.cdi.CDI;

/**
 * COncentra a lógica de execução dos interceptadores de {@link LoadData} e {@link LoadDatas}
 * @author Augusto
 *
 */
public class LoadDataExecutor {

	/**
	 * Verifica a presença das anotações {@link LoadData} e {@link LoadDatas} no método interceptado ou na classe
	 * que o contém, processando-as.
	 * @param method Método interceptado.
	 */
	public void process(Method method) throws Exception {
		
		if (mustLoadData(method)) {
			Collection<DataLoaderPrecedence> dataLoaders =  extractDataLoaders(method);
			
			// executando sequencialmente para garantir a precedência
			for (DataLoader dataLoader : dataLoaders) {
				dataLoader.load();
			}
		}		
		
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
			SqlDataLoader scriptDataLoader = CDI.lookup(SqlDataLoader.class);
			scriptDataLoader.setScriptPath(script);
			
			DataLoaderPrecedence dataLoader = new DataLoaderPrecedence(loadData.precedence(), scriptDataLoader);
			loaders.add(dataLoader);
		}
		
		// carregando beans
		Class<? extends DataLoader>[] beans = loadData.dataLoader();
		for (Class<? extends DataLoader> dataLoaderType : beans) {
			DataLoader beanDataLoader = CDI.lookup(dataLoaderType);
			
			DataLoaderPrecedence dataLoader = new DataLoaderPrecedence(loadData.precedence(), beanDataLoader);
			loaders.add(dataLoader);
		}
	}
	
	/**
	 * Determina se o método que está sendo executado possui alguma DataLoader configurado, seja diretamente
	 * ou na classe que o declara.
	 * @param method Método que está sendo interceptado.
	 * @return true caso o método esteja dentro das condições para disparar o carregamento de dados.
	 */
	protected boolean mustLoadData(Method method) {
		return method.isAnnotationPresent(LoadData.class) 
				|| method.getDeclaringClass().isAnnotationPresent(LoadData.class)
				|| method.isAnnotationPresent(LoadDatas.class)	
				|| method.getDeclaringClass().isAnnotationPresent(LoadDatas.class); 
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
