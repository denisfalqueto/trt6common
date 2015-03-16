package br.jus.trt.lib.qbe.repository.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;

import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.FetchMode;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.util.ReflectionUtil;

/**
 * Responsável por realizar o processamento de fetches configurados em um {@link QBEFilter}.
 * Uma instância de {@link FetchesProcessor} deve ser utilizada para o processamento de cada
 * consulta, visto que esta entidade armazena estado sobre a análise do {@link QBEFilter}.
 * @author augusto
 */
public abstract class FetchesProcessor implements IProcessor {

	private Filter<?> filter;
	private Criteria criteria;
	private Map<String, String> aliasCache;
	
	public FetchesProcessor(Criteria criteria, Filter<?> filter,
			Map<String, String> aliasCache) {
		super();
		this.criteria = criteria;
		this.filter = filter;
		this.aliasCache = aliasCache;
	}

	/**
	 * Realiza o processamento do fetch adequadamente
	 */
	public abstract void process();
	
	/**
	 * verifica se a propriedade configurada para fetch é uma coleção ou possui um coleção no seu caminho
	 */
	protected boolean hasCollection(FetchMode fetch) {
		
		// quebra a propriedade, caso seja aninhada
		String[] split = fetch.getProperty().split("\\.");
		boolean isCollection = false;
		
		// verifica cada trecho da propriedade procurando por coleções
		String partialProperty = "";
		for (int i = 0; i < split.length && !isCollection; i++) {
			partialProperty += split[i];
			isCollection = isCollection(partialProperty);
			
			partialProperty += "."; // adiciona um ponto para compor propriedades aninhadas
		}
		
		return isCollection;
	}
	
	protected boolean isCollection(String property) {
		Class<?> propType = ReflectionUtil.getFieldType(filter.getEntityClass(), property);
		return Collection.class.isAssignableFrom(propType);
	}
		
	
	/**
	 * Quebra as propriedades configuradas para fetch de forma aninhada em propriedades individuais.
	 * Ex: servidor.dependentes.cidade = [servidor, servidor.dependentes, servidor.dependentes.cidade] 
	 * @return Nova lista de {@link FetchMode} com as propriedades aninhadas individualizadas.
	 */
	protected List<FetchMode> splitFetchProperties() {
		
		List<FetchMode> splitted = new ArrayList<FetchMode>();
		
		for (FetchMode fetchMode : getFilter().getFetches()) {
			String lastProperty = ""; 
			for (String fetchProperty : fetchMode.getProperty().split("\\.")) {
				String splittedProperty = lastProperty.isEmpty() ? fetchProperty : lastProperty + fetchProperty;
				splitted.add(new FetchMode(splittedProperty, fetchMode.getJoinType()));
				lastProperty += fetchProperty + ".";
			}
		}
		
		return splitted;
	}

	protected Criteria getCriteria() {
		return criteria;
	}

	protected Filter<?> getFilter() {
		return filter;
	}

	protected Map<String, String> getAliasCache() {
		return aliasCache;
	}
}
