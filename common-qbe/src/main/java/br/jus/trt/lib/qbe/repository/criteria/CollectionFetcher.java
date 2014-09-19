package br.jus.trt.lib.qbe.repository.criteria;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.Identifiable;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.api.exception.QbeException;
import br.jus.trt.lib.qbe.repository.criteria.FetchesManualProcessor.PropertyGroup;
import br.jus.trt.lib.qbe.util.ReflectionUtil;

/**
 * Determina o comportamento geral de fetchers de coleções, que deverão ser utilizados
 * para o carregamento antecipado de associações com coleções em consultas com QBE
 */
public abstract class CollectionFetcher {

	private Map<Identifiable, Collection<Identifiable>> fetchResultMap;
	private PropertyGroup groupToFetch;
	private Filter<?> filter;
	private List<?> inProjectionToCollectionFetch;
	private Session session;
	private OperatorProcessorRepository operatorProcessorRepository;
	

	/**
	 * @param operatorProcessorRepository 
	 * @param session Sessão do hibernate para realização de fetch
	 * @param fetchResultMap Mapa para preenchimento com o resultado do fetch
	 * @param groupToFetch Grupo com as propriedades para realização deste fetch
	 * @param filter Filtro que solicitou o fetch
	 * @param inProjectionToCollectionFetch Lista com o conjunto de valores a serem considerados na operação IN, para fetch parcial
	 */
	public CollectionFetcher(
			OperatorProcessorRepository operatorProcessorRepository, 
			Session session, 
			Map<Identifiable, Collection<Identifiable>> fetchResultMap,
			Filter<?> filter,
			PropertyGroup groupToFetch,
			List<?> inProjectionToCollectionFetch) {
		super();
		this.operatorProcessorRepository = operatorProcessorRepository;
		this.session = session;
		this.fetchResultMap = fetchResultMap;
		this.filter = filter;
		this.groupToFetch = groupToFetch;
		this.inProjectionToCollectionFetch = inProjectionToCollectionFetch;
	}

	/**
	 * Realiza a consulta dos objetos da coleção mapeada para fetch. 
	 * @param partialFetchResultMap Mapa para preenchimento com o resultado do fetch parcial
	 * @param groupToFetch Grupo com as propriedades para realização deste fetch parcial
	 * @param entityCollectionType Tipo da entidade mapeada na coleção
	 * @param inProjectionToCollectionFetch Lista com o conjunto de valores a serem considerados na operação IN, para fetch parcial
	 */
	public abstract void fetch();

	/**
	 * Procura o tipo da entidade mapeada na coleção que se deseja realizar fetch.
	 * @param property Nome da propriedade.
	 * @return Tipo da entidade associada através da coleção.
	 */
	@SuppressWarnings("unchecked")
	protected Class<Identifiable> findCollectionEntityType(String property) {
		try {
			Field field = ReflectionUtil.getField(getFilter().getEntityClass(), property);
			ParameterizedType typeList = (ParameterizedType) field.getGenericType();
			Type collectionType = typeList.getActualTypeArguments()[0];
			return (Class<Identifiable>) collectionType;
		} catch (Exception e) {
			throw new QbeException("Não foi possível encontrar informações sobre a entidade contida na coleção " 
					+ getFilter().getEntityClass().getSimpleName() + "." + property, e);
		}
	}
	
	/**
	 * Adiciona o registro encontrado à coleção cujo fetch foi solicitado.
	 * @param primaryEntity Entidade primária que contém a coleção cujo fetch foi colicitado. 
	 * @param fetchedEntity Objeto encontrado que deverá pertencer à coleção que foi solicitada para realização de fetch
	 */
	protected void addToFetchResultMap(Identifiable primaryEntity, Identifiable fetchedEntity) {
		Collection<Identifiable> associationList = getFetchResultMap().get(primaryEntity);
		if (associationList == null) {
			associationList = FetchesManualProcessor.createAppropriateCollection(getFilter().getEntityClass(), getGroupToFetch());
			getFetchResultMap().put(primaryEntity, associationList);
		}

		associationList.add(fetchedEntity);
	}
	
	public Map<Identifiable, Collection<Identifiable>> getFetchResultMap() {
		return fetchResultMap;
	}

	protected void setFetchResultMap(
			Map<Identifiable, Collection<Identifiable>> fetchResultMap) {
		this.fetchResultMap = fetchResultMap;
	}

	public PropertyGroup getGroupToFetch() {
		return groupToFetch;
	}

	protected void setGroupToFetch(PropertyGroup groupToFetch) {
		this.groupToFetch = groupToFetch;
	}

	public List<?> getInProjectionToCollectionFetch() {
		return inProjectionToCollectionFetch;
	}

	protected void setInProjectionToCollectionFetch(
			List<?> inProjectionToCollectionFetch) {
		this.inProjectionToCollectionFetch = inProjectionToCollectionFetch;
	}

	public Filter<?> getFilter() {
		return filter;
	}

	protected void setFilter(Filter<?> filter) {
		this.filter = filter;
	}

	public Session getSession() {
		return session;
	}

	protected void setSession(Session session) {
		this.session = session;
	}

	public OperatorProcessorRepository getOperatorProcessorRepository() {
		return operatorProcessorRepository;
	}

}
