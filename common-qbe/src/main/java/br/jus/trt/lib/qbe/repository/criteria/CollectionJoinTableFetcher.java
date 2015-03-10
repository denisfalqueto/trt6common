package br.jus.trt.lib.qbe.repository.criteria;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.FetchMode;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.Identifiable;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.repository.criteria.FetchesManualProcessor.PropertyGroup;
import br.jus.trt.lib.qbe.util.EntityUtil;
import br.jus.trt.lib.qbe.util.ReflectionUtil;
import br.jus.trt.lib.qbe.util.StringUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Realiza o fetch de coleções mapeadas através de "@JoinTable". 
 * <br/>  
 * Ex:  
 *  
 *  <br/>
 *	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
 *  <br/>
 *	@JoinTable(name="TB_PROJETO_FERRAMENTA",
 *			joinColumns=@JoinColumn(name="ID_PROJETO"),
 *			inverseJoinColumns=@JoinColumn(name="ID_FERRAMENTA"))
 *  <br/>
 *	private List<Ferramenta> ferramentas;
 */
public class CollectionJoinTableFetcher extends CollectionFetcher {

        private Logger log = LogManager.getLogger();
	
	public CollectionJoinTableFetcher(
			OperatorProcessorRepository operatorProcessorRepository, 
			Session session,
			Map<Identifiable, 
			Collection<Identifiable>> partialFetchResultMap,
			Filter<?> filter, 
			PropertyGroup groupToFetch,
			List<?> inProjectionToCollectionFetch) {
		super(operatorProcessorRepository, session, partialFetchResultMap, filter, groupToFetch, inProjectionToCollectionFetch);
	}

	/**
	 * Na consulta com joinTable, não é possível navegar entre as entidades, visto que não há uma relação direta entre elas.
	 * Portanto, a única forma de encontrar o par ordenado (entidade1, entidade2) é realizar uma consulta com projeção.
	 * A consulta será realizada a partir da entidade com o tipo encontrado no {@link QBEFilter}, assim teremos certeza de 
	 * que há um relacionamento OneToMany deste lado.
	 */
	@Override
	public void fetch() {
                log.entry();
		
		/*
		 * Identifica qual será a entidade principal para a consulta que realizará o fetch manual.
		 * Se for a entidade principal da consulta original (encontrada em QBEFilter), o fetch será realizado
		 * de forma transparente, visto que todas as propriedades já são relativas a esta entidade. Porém,
		 * se a coleção para fetch estiver mais próximo a um dos atributos da entidade principal do QBEFilter,
		 * então deverá ser necessário reajustar as propriedades para que fiquem relativas ao "novo" tipo
		 * primário.
		 * 
		 * Ex: Considere a consulta "from Projeto fetch ferramentas". A coleção "ferramentas" está diretamente associada ao tipo
		 * principal da consulta "Projeto". Então o caminho "projeto.ferramentas" é válido. 
		 * 
		 * Ex: Considere a consulta "from ProjetoServidor fetch projeto.ferramentas". Como "ferramentas" está mais próximo de "projeto" 
		 * e não de "ProjetoServidor", a consulta para fetch manual será realizada sobre "Projeto". Neste caso, a propriedade "projeto.ferramentas"
		 * não é valida para a entidade "Projeto", e deverá ser reajustada para que seu caminho relativo seja válido, ou seja, apenas "ferramenta". 
		 * 
		 */
		Class<?> entityType = findBaseEntityTypeForFetch();
		
		PropertyGroup groupToFetch = getGroupToFetch();
		if (!entityType.equals(getFilter().getEntityClass())) {
                        log.debug("Entidade diferente da entidade do filtro");
			// ajusta o caminho relativo
			groupToFetch = groupToFetch.clone();
			
			String property = groupToFetch.getPrimaryProperty().getProperty();
			String projectionKey = getProjectionProperty(property);
			String tofixRelativeProperty = projectionKey + ".";
			
			adjustRelativeProperty(groupToFetch.getPrimaryProperty(), tofixRelativeProperty);
			
			for (FetchMode nested : groupToFetch.getNestedProperties()) {
				adjustRelativeProperty(nested, tofixRelativeProperty);
			}
		}
		
		
		@SuppressWarnings("unchecked")
		List<Object> ids = EntityUtil.getIds((List<? extends Identifiable>) getInProjectionToCollectionFetch());
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		QBEFilter<?> filter = new QBEFilter(entityType);
		filter.setForcePreFetchCollection(true); // para forçar o fetch da coleção da maneira tradicional do hibernate, otimizando a consulta
		filter.filterBy(StringUtil.getFakePath("id"), Operators.in(), ids); 		// restringindo os registros ao conjunto encontrado na consulta principal
		
		
		filter.addFetch(StringUtil.getFakePath(groupToFetch.getPrimaryProperty().getProperty()));				// fetch da colecao anotada com @JoinTable
		filter.addFetch(groupToFetch.getNestedProperties().toArray(new FetchMode[0])); // fetch das propriedades aninhadas
		
		CriteriaQBEProcessor<Identifiable> qbeProcessor = new CriteriaQBEProcessor<Identifiable>(getOperatorProcessorRepository(), getSession(), filter);
		List<Identifiable> fetchedList = qbeProcessor.search();
		
		// incluindo a lista carregada no mapa, evitando repetições
		Set<Identifiable> included = new HashSet<Identifiable>();
		for (Identifiable entity : fetchedList) {
			if (!included.contains(entity)) {
				included.add(entity);
				
				// recuperando a coleção que foi "fetched"
				@SuppressWarnings("unchecked")
				Collection<Identifiable> items = (Collection<Identifiable>) ReflectionUtil.getValue(entity, groupToFetch.getPrimaryProperty().getProperty());
				for (Identifiable fetchedItem : items) {
					addToFetchResultMap(entity, fetchedItem);
				}
			}
		}
		
	}

	private void adjustRelativeProperty(FetchMode primaryProperty, String tofixRelativeProperty) {
                log.entry(primaryProperty, tofixRelativeProperty);
		String newRelativeProperty = primaryProperty.getProperty().replaceFirst(tofixRelativeProperty, "");
		primaryProperty.setProperty(newRelativeProperty);
	}

	/**
	 * Descobre o tipo da entidade que é dona imadiata do relacionamento para fetch de coleção. 
	 * Ex: "from ProjetoServidor fetch projeto.ferramentas", o dono imediato do relacionamento com a coleção para fetch é
	 * Projeto.  
	 * @return tipo da entidade que é dona imadiata do relacionamento para fetch de coleção.
	 */
	private Class<?> findBaseEntityTypeForFetch() {
                log.entry();
		
		FetchMode primaryProperty = getGroupToFetch().getPrimaryProperty();
		
		Class<?> baseEntityType;
		if (!primaryProperty.getProperty().contains(".")) {
                        log.debug("primaryProperty não contém ponto");
			// é uma associação primária (não aninhada), a entidade base é a entidade associada ao QbeFitler
			baseEntityType = getFilter().getEntityClass();
			
		} else {
                        log.debug("primaryProperty possui ponto");
			/* é uma associação aninhada, é preciso descobrir o tipo de um dos atributos da entidade associada ao QBEFilter */
			
			// a coleção para fetch é identifica pelo último token da propriedade primária, removendo-a temos
			// a chave para nossa projeção
			String property = primaryProperty.getProperty();
			String projectionKey = getProjectionProperty(property);
			
			baseEntityType = ReflectionUtil.getFieldType(getFilter().getEntityClass(), projectionKey);
		}

		return baseEntityType;
		
		
	}

	private String getProjectionProperty(String property) {
		return property.substring(0, property.lastIndexOf("."));
	}

}
