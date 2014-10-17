package br.jus.trt.lib.qbe.repository.criteria;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import br.jus.trt.lib.qbe.api.FetchMode;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.Identifiable;
import br.jus.trt.lib.qbe.api.JoinType;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.exception.QbeException;
import br.jus.trt.lib.qbe.util.PropertyComparator;
import br.jus.trt.lib.qbe.util.ReflectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Realiza o fetch manual das coleções configuradas. Fetch manual significa realizar uma segunda consulta manualmente, simulando o funcionamento
 * do fetch do hibernate. 
 *  
 * Deve ser processado após a consulta principal.
 * 
 * @author augusto
 */
public class FetchesManualProcessor extends FetchesProcessor {

	protected final static Logger log = LogManager.getLogger(QBERepository.class);
	
	/** lista o resultado da consulta principal */
	private List<Identifiable> resultList;
	private Session session;

	private OperatorProcessorRepository operatorProcessorRepository;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FetchesManualProcessor(OperatorProcessorRepository operatorProcessorRepository, Session session, List resultList, Criteria criteria, Filter<?> filter, Map<String, String> aliasCache) {
		super(criteria, filter, aliasCache);
		this.operatorProcessorRepository = operatorProcessorRepository;
		this.session = session;
		this.resultList = resultList;
	}

	public void process() {
		
		if (resultList != null && !resultList.isEmpty() 
				&& !getFilter().isForcePreFetchCollection()) {
			
			List<FetchMode> collectionsToFetch = searchCollectionsToFetch();
			List<PropertyGroup>  groupedCollections = groupCollections(collectionsToFetch);
			
			if (groupedCollections != null) {
				for (PropertyGroup colToFetch : groupedCollections) {
					System.out.println("Processar fetch para " + colToFetch);
					fetch(colToFetch);
				}
			}
			
		}	
	}

	/**
	 * Implementa Fetch para a propriedade configurada
	 * @param groupToFetch Coleção e propriedades aninhadas para realizar fetch manual
	 */
	private void fetch(PropertyGroup groupToFetch) {
		// lista com filtro para carregamento da coleção (fetch) 
		List<?> inProjectionToCollectionFetch = resolveInProjection(groupToFetch.getPrimaryProperty());
		
		if (!inProjectionToCollectionFetch.isEmpty()) {
			/*
			 * O banco de dados suporta apenas 1000 registro na operação IN, portanto, vamos ter que realizar fetch parcial, em grupos de 1000
			 */
			
			// Mapa para agrupamento do resultado das consultas realizadas em partialFetch
			Map<Identifiable, Collection<Identifiable>> fetchResultMap = new HashMap<Identifiable, Collection<Identifiable>>();
			
			// indices para manter-se dentro dos limites do banco de dados
			int startGroup = 0;
			int endGroup = inProjectionToCollectionFetch.size() > 1000 ? 1000 : inProjectionToCollectionFetch.size();

			do {
				partialFetch(fetchResultMap, groupToFetch, inProjectionToCollectionFetch.subList(startGroup, endGroup));
				
				// atualiza indices
				startGroup = endGroup;
				endGroup = inProjectionToCollectionFetch.size() > endGroup + 1000 ? endGroup + 1000 : inProjectionToCollectionFetch.size();   
			} while (startGroup < inProjectionToCollectionFetch.size());
			
			// atualiza as entidades encontradas na consulta principal, preenchendo-as com o resultado
			// do fetch realizado
			updateEntitiesWithFetchResult(fetchResultMap, groupToFetch);
		}	
	}

	/**
	 * Realiza fetch partial da coleção mapeada para fetch. Chamamos de fetch parcial por que é considerado o número de registros
	 * retornado na consulta principal, levando em consideração que o banco de dados (oracle) suporta apenas 1000 elementos no operador IN.
	 * @param partialFetchResultMap Mapa para preenchimento com o resultado do fetch parcial
	 * @param groupToFetch Grupo com as propriedades para realização deste fetch parcial
	 * @param entityCollectionType Tipo da entidade mapeada na coleção
	 * @param inProjectionToCollectionFetch Lista com o conjunto de valores a serem considerados na operação IN, para fetch parcial
	 */
	private void partialFetch(Map<Identifiable, Collection<Identifiable>> partialFetchResultMap, PropertyGroup groupToFetch, List<?> inProjectionToCollectionFetch) {
		CollectionFetcher collectionFetcher = CollectionFetcherFactory.create(this.operatorProcessorRepository, this.session, partialFetchResultMap, getFilter(), groupToFetch, inProjectionToCollectionFetch);
		collectionFetcher.fetch();
	}

	/**
	 * No mapeamento de coleções é mais comum a utilização de List, porém é permitido
	 * o uso de outros tipos de coleções (como Set), portanto, é necessário descobrir
	 * o tipo utilizado na entidade que contém o mapeamento.
	 * @param beanType Tipo do bean que contém as propriedades
	 * @param groupToFetch Propriedade que representa a coleção mapeada.
	 * @return Um tipo concreto de coleção compatível com o tipo utilizado na entidade que detém o mapeamento. 
	 */
	public static Collection<Identifiable> createAppropriateCollection(Class<?> beanType, PropertyGroup groupToFetch) {
		Class<Collection<?>> collectionType = findCollectionType(beanType, groupToFetch.getPrimaryProperty().getProperty());
		
		Collection<Identifiable> collection = null;
		
		if (List.class.isAssignableFrom(collectionType)) {
			collection = new ArrayList<Identifiable>();
			
		} else if (Set.class.isAssignableFrom(collectionType)) {
			collection = new HashSet<Identifiable>();
			
		} else {
			throw new QbeException("O tipo de coleção utilizado no mapeamento não é suportado: " + groupToFetch);
			
		}
		
		return collection;
	}

	/**
	 * atualiza as entidades encontradas na consulta principal, preenchendo-as com o resultado do fetch realizado.
	 * @param partialFetchResult Mapa que contém o resultado do fetch realizado.
	 * @param groupToFetch Grupo com as informações sobre a coleção para realização de fetch
	 */
	private void updateEntitiesWithFetchResult(Map<Identifiable, Collection<Identifiable>> partialFetchResult, PropertyGroup groupToFetch) {
		// atualiza as entidades principais com os valores encontrados
		for (Iterator<Identifiable> iterator = resultList.iterator(); iterator.hasNext();) {
			Identifiable primaryEntity = iterator.next();
			
			Identifiable projectionKey = resolveProjectionValue(primaryEntity, groupToFetch.getPrimaryProperty());
			
			Collection<Identifiable> fetchedList = partialFetchResult.get(projectionKey);
			if (fetchedList == null) {
				
				// se não encontrou registros na associação e o fetch
				// era do INNER, o registro deve ser removido do resultado.
				if (JoinType.INNER.equals(groupToFetch.primaryProperty.getJoinType())) {
					iterator.remove();
					log.warn("A utilização de FETCH + INNER pode provocar diferenças de resultados entre a consulta de registros e o count: " + groupToFetch.primaryProperty);
					continue; // ABANDONA ESTA ITERACAO
				} 
				
				// garante ao menos uma lista vazia
				fetchedList = createAppropriateCollection(getFilter().getEntityClass(), groupToFetch);
			} 
			
			try {
				/*
				 * O hibernate implementa um controle mais restrito para associação com cascade delete orphan, reclamando quando
				 * tentamos substituir a instância da coleção mapeada. Para estes casos, será necessário remover a entidade
				 * owner do relacionamento do cache, desabilitando o controle do hibernate. Como consequência, não será funcionará
				 * o lazy initialization - que deve ser substituído por um fetch.
				 */
				if (isDeleteOrphan(groupToFetch.getPrimaryProperty())) {
					session.evict(projectionKey);
					log.info("A associação " + getFilter().getEntityClass().getSimpleName() + "." +  groupToFetch.getPrimaryProperty().getProperty() 
							+ " está mapeada como DELETE ORPHAN. Foi necessário remover o owner da relação do cache do hibernate. Isto não" +
							"causará problemas mas o comportamento de LazyInitialization por desabilitado para este objeto. ");
				} 

				// atualiza a associação com a lista recuperada
				safeUpdateValue(groupToFetch, primaryEntity, fetchedList);
				
			} catch (Exception e) {
				throw new QbeException("Falha ao tentar atualiza a entidade com a coleção carregada manualmente: " 
										 + groupToFetch.getPrimaryProperty(), e);
			}
		}
	}

	/**
	 * O objeto que receberá a lista pode ser um hibernate proxy, é importante realizar a atualização
	 * diretamente no objeto original. Caso contrário, alguma vezes o proxy não repassa o valor para o objeto.
	 */
	private void safeUpdateValue(PropertyGroup groupToFetch, Identifiable primaryEntity, Collection<Identifiable> fetchedList) {
		// recupe
		Identifiable projectionValue = resolveProjectionValue(primaryEntity, groupToFetch.getPrimaryProperty());
		Identifiable realEntity = ReflectionUtil.unProxy(projectionValue);
		
		int collectionIndex = groupToFetch.getPrimaryProperty().getProperty().lastIndexOf(".") + 1;
		int endIndex = groupToFetch.getPrimaryProperty().getProperty().length();
		String collectionProperty = groupToFetch.getPrimaryProperty().getProperty().substring(collectionIndex, endIndex);
		
		ReflectionUtil.setValue(realEntity, collectionProperty, fetchedList);
	}

	/**
	 * Verifica se a associação configurada está mapeada para operar com "DELETE ORPHAN".
	 * 
	 * @see CascadeType#DELETE_ORPHAN
	 * @see OneToMany#orphanRemoval()
	 * @see OneToOne#orphanRemoval()
	 */
	private boolean isDeleteOrphan(FetchMode primaryProperty) {

		Field field = ReflectionUtil.getField(getFilter().getEntityClass(), primaryProperty.getProperty());
		boolean isDeleteOrphan = false;
		
		// DeleteOrpahn pode ser configurado através da anotação @Cascade
		Cascade cascade = field.getAnnotation(Cascade.class);
		if (cascade != null) {
			CascadeType[] types = cascade.value();
			for (CascadeType cascadeType : types) {
				if (CascadeType.DELETE_ORPHAN.equals(cascadeType)) {
					isDeleteOrphan = true;
					break;
				}
			}
		}
		
		// tambem pode ser configurado como atributo de um @OneToMany
		if (!isDeleteOrphan && field.isAnnotationPresent(OneToMany.class)) {
			OneToMany oneToMany = field.getAnnotation(OneToMany.class);
			isDeleteOrphan = oneToMany.orphanRemoval();
		}	
		
		// ou @OneToOne
		if (!isDeleteOrphan && field.isAnnotationPresent(OneToOne.class)) {
			OneToOne oneToOne = field.getAnnotation(OneToOne.class);
			isDeleteOrphan = oneToOne.orphanRemoval();
		}	
		
		
		
		return isDeleteOrphan;
	}

	/**
	 * Identifica os objetos para filtro dos registros da associação.
	 * Quando a coleção mapeada está na raiz da entidade associada à consulta principal, o próprio resultado
	 * da consulta poderá ser utilizado como filtro para identificar o registros da coleção mapeada:
	 * <br/>
	 * Ex: Considere que a entidade principal é servidor, e relação é dependentes. Para encontrar os dependentes dos servidores basta
	 * realizar uma consulta do tipo: from dependentes d where d.servidor in (:servidores)
	 * <br/><br/>
	 * Porém, quando a coleção não é um atributo da entidade principal, mas é um atributo aninhado, precisamos identificar uma projeção:
	 * <br/>
	 * Ex: Considere a entidade principal ProjetoServidor, e a relação é servidor.dependentes. Para encontrar os dependentes,
	 * precisamos primeiro identificar todos os servidores existentes em ProjetoServidor, só então realizar a busca dos depententes.  
	 * @param primaryProperty Propriedade que contém o caminho para a coleção associada. Se este caminho tem apenas um nível, significa que
	 * a associação é primárica, ou seja, a coleção é um atributo da entidade principal. Se tiver mais de um nível, significa que a associação
	 * é aninhada.
	 * @return Lista de valores estraídos do resultado da consulta principal ({@link FetchesManualProcessor#resultList}), que representam o filtro
	 * necessário para carregar a coleção solicitada no fetch.
	 */
	private List<?> resolveInProjection(FetchMode primaryProperty) {
		List<Identifiable> inProjection = new ArrayList<Identifiable>();
		
		for (Identifiable object : resultList) {
			Identifiable projectionValue = resolveProjectionValue(object, primaryProperty);
			
			if (projectionValue != null && !inProjection.contains(projectionValue)) {
				inProjection.add(projectionValue);
			}
		}
		
		return inProjection;
	}

	/**
	 * Identifica qual o objeto responsável pela associação direta com a coleção configurada para fetch.
	 * Caso a coleção esteja associada diretamente à entidade principal (da consulta principal), a própria
	 * entidade é relacionada como objeto da projeção. Caso a coleção faça parte de uma associação aninhada 
	 * (ex: orgao.uf.cidades), retorna o objeto responsável pela relação direta com a coleção (no caso, "orgao.uf").
	 * @param primaryProperty Propriedade primária que indica o caminho para a coleção.
	 * @param resultListObject Objeto do resultado da consulta principal, para avaliação da projeção.
	 * @return Objeto responsável pela projeção, seja o próprio objeto resultado da consulta, ou uma propriedade
	 * intermediária.
	 */
	private Identifiable resolveProjectionValue(Identifiable resultListObject, FetchMode primaryProperty) {
		Identifiable projectionValue;
		if (!primaryProperty.getProperty().contains(".")) {
			// é uma associação primária, o filtro é a própria consulta principal
			projectionValue = resultListObject;
			
		} else {
			/* é uma associação aninhada, é preciso realizar uma projeção sobre a consulta principal */
			
			// a coleção é identifica pelo último token da propriedade primária, removendo-a temos
			// a chave para nossa projeção
			String property = primaryProperty.getProperty();
			String projectionKey = getProjectionProperty(property);
			
			projectionValue = (Identifiable) ReflectionUtil.getValue(resultListObject, projectionKey);
		}
		return projectionValue;
	}

	private String getProjectionProperty(String property) {
		return property.substring(0, property.lastIndexOf("."));
	}

	/**
	 * Procura o tipo da coleção que se deseja realizar fetch.
	 * @param beanType Tipo do bean que contém as propriedades.
	 * @param property Nome da propriedade.
	 * @return Tipo da coleção.
	 */
	@SuppressWarnings("unchecked")
	private static Class<Collection<?>> findCollectionType(Class<?> beanType, String property) {
		try {
			Field field = ReflectionUtil.getField(beanType, property);
			return (Class<Collection<?>>) field.getType();
		} catch (Exception e) {
			throw new QbeException("Não foi possível encontrar informações sobre o tipo da coleção " 
					+ beanType.getSimpleName() + "." + property, e);
		}
	}

	/**
	 * Algumas propriedades pode ser agrupadas para serem retornadas em uma única consulta, quando se tratam de aninhamento com uma coleção em comum.
	 * <br/> 
	 * Ex: dependentes, dependentes.cidade e dependentes.cidade.uf
	 * <br/>
	 * Podemos considerar que temos uma propriedade primária que sempre representa a coleção base (dependentes) e outras aninhadas (cidade, cidade.uf) 
	 * @param collectionsToFetch Lista com todas as propriedades que contém coleção para fetch.
	 * @return Lista agrupada, contento apenas uma única instância de cada coleção.
	 */
	private List<PropertyGroup> groupCollections(List<FetchMode> collectionsToFetch) {
		// ordena as propriedades, garantindo que as relacionadas estarão próximas
		PropertyComparator.sort(collectionsToFetch, "property");
		
		
		List<PropertyGroup> groups = new ArrayList<FetchesManualProcessor.PropertyGroup>();
		for (FetchMode fetchMode : collectionsToFetch) {
			
			String[] properties = fetchMode.getProperty().split("\\.");
			
			// identifica a coleção base da propriedade
			String baseCollectionProp = "";
			int index = 0;
			do {
				baseCollectionProp += baseCollectionProp.isEmpty() ? properties[index] : "." + properties[index];
				index++;
			} while (!isCollection(baseCollectionProp) && index < properties.length);
			
			// cria um grupo de propriedades, identificando a coleção base como propriedade primária
			PropertyGroup group = new PropertyGroup(new FetchMode(baseCollectionProp, fetchMode.getJoinType()));
			
			// evita duplicações
			int ig = groups.indexOf(group);
			if (ig >= 0) {
				group = groups.get(ig);
			} else {
				groups.add(group);
			}
			
			
			// adiciona as demais propriedades como propriedades secundárias da coleção
			if (properties.length > index) {
				String lastProperty = null;
				for (int i = index; i < properties.length; i++) {
					String nestedProperty = lastProperty == null ? properties[i] : lastProperty + properties[i];
					group.addNestedProperties(new FetchMode(nestedProperty, fetchMode.getJoinType()));
					lastProperty = nestedProperty + ".";
				}
			}
			
		}
		
		return groups;
	}

	/**
	 * Analisa a lista de fetches configurada no filtro e decide quais devem ser implementados manualmente.
	 * @param filerFetches
	 * @return Lista de fetches que devem ser executados manualmente.
	 */
	private List<FetchMode> searchCollectionsToFetch() {
		
		/*
		 * Apenas as coleções devem sofrer fetch manual.
		 */
		List<FetchMode> collectionsToFetch = new ArrayList<FetchMode>();
		for (FetchMode fetch : splitFetchProperties()) {
			
			boolean isCollection = hasCollection(fetch);
			if (isCollection) {
				collectionsToFetch.add(fetch);
			} 			
		}
		
		return collectionsToFetch;
	}
	
	/**
	 * Encapsula as propriedades inter-relacionadas para realização de fetch. Ex: projetos, projetos.servidor
	 * @author augusto
	 */
	class PropertyGroup implements Cloneable {
		
		/** Propriedade primária que representa a coleção */
		private FetchMode primaryProperty;
		
		/** Propriedades aninhadas baseadas na propriedade primária */
		private List<FetchMode> nestedProperties = new ArrayList<FetchMode>();
		
		public PropertyGroup(FetchMode primaryProperty) {
			this.primaryProperty = primaryProperty;
		}

		public FetchMode getPrimaryProperty() {
			return primaryProperty;
		}
		
		public void setPrimaryProperty(FetchMode primaryProperty) {
			this.primaryProperty = primaryProperty;
		}

		public List<FetchMode> getNestedProperties() {
			return nestedProperties;
		}

		public void addNestedProperties(FetchMode nestedProperty) {
			if (!this.nestedProperties.contains(nestedProperty)) {
				this.nestedProperties.add(nestedProperty);
			}	
		}
		
		@Override
		public String toString() {
			return primaryProperty + (nestedProperties.isEmpty() ? "" : "." + nestedProperties);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((primaryProperty == null) ? 0 : primaryProperty
							.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PropertyGroup other = (PropertyGroup) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (primaryProperty == null) {
				if (other.primaryProperty != null)
					return false;
			} else if (!primaryProperty.equals(other.primaryProperty))
				return false;
			return true;
		}

		private FetchesManualProcessor getOuterType() {
			return FetchesManualProcessor.this;
		}
		
		@Override
		public PropertyGroup clone() {
			PropertyGroup propertyGroupClone = new PropertyGroup(getPrimaryProperty() != null ? getPrimaryProperty().clone() : null);
			
			for (FetchMode nested : getNestedProperties()) {
				propertyGroupClone.addNestedProperties(nested.clone());
			}
			
			return propertyGroupClone;
		}
		
	}
	
}