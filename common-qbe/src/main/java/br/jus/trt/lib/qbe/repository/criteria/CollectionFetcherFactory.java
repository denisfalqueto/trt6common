package br.jus.trt.lib.qbe.repository.criteria;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.Session;

import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.Identifiable;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.api.exception.QbeException;
import br.jus.trt.lib.qbe.repository.criteria.FetchesManualProcessor.PropertyGroup;
import br.jus.trt.lib.qbe.util.ReflectionUtil;
import br.jus.trt.lib.qbe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fábrica de Fetchers, criando uma instância de acordo com as características
 * da propriedade para fetch. 
 * @author augusto
 */
public class CollectionFetcherFactory {
    
        private static final Logger log = LogManager.getLogger();

	public static CollectionFetcher create(OperatorProcessorRepository operatorProcessorRepository, 
			Session session, 
			Map<Identifiable, Collection<Identifiable>> fetchResultMap, 
			Filter<?> filter, 
			PropertyGroup groupToFetch, 
			List<?> inProjectionToCollectionFetch) {

		CollectionFetcher fetcher = null;
		
		if (isMappedByCollection(filter,groupToFetch)) {
			fetcher = new CollectionMappedByFetcher(operatorProcessorRepository, session, fetchResultMap, filter, groupToFetch, inProjectionToCollectionFetch);
		} else if (isJoinTableCollection(filter, groupToFetch)) {
			fetcher = new CollectionJoinTableFetcher(operatorProcessorRepository, session, fetchResultMap, filter, groupToFetch, inProjectionToCollectionFetch);
		}
		
		return fetcher;
	}
	
	private static boolean isMappedByCollection(Filter<?> filter, PropertyGroup groupToFetch) {
                log.entry(filter, groupToFetch);
		/*
		 * Relacionamentos com collections são implementados, geralmente, com OneToMany
		 */
		String colProperty = groupToFetch.getPrimaryProperty().getProperty();
		try {
			Field colField = ReflectionUtil.getField(filter.getEntityClass(), colProperty);
			
			if (colField.isAnnotationPresent(OneToMany.class)) {
                                log.debug("Possui anotação OneToMany");
				OneToMany oneToMany = colField.getAnnotation(OneToMany.class);
				return log.exit(!StringUtil.isStringEmpty(oneToMany.mappedBy()));
				
			} else {
                                throw log.throwing(new QbeException(
                                        "Não foi encontrado a anotação @OneToMany na relação ."
                                        + colProperty));
			}
			
		} catch (QbeException e) {
			throw log.throwing(e);
			
		} catch (Exception e) {
			throw log.throwing(new QbeException("Não foi possível encontrar informações sobre a coleção " 
					+ filter.getEntityClass().getSimpleName() + "." + colProperty, e));
		}
	}

	private static boolean isJoinTableCollection(Filter<?> filter, PropertyGroup groupToFetch) {
                log.entry(filter, groupToFetch);
		
		String colProperty = groupToFetch.getPrimaryProperty().getProperty();
		try {
			Field colField = ReflectionUtil.getField(filter.getEntityClass(), colProperty);
			return log.exit(colField.isAnnotationPresent(JoinTable.class));
		} catch (Exception e) {
			throw log.throwing(new QbeException("Não foi possível encontrar informações sobre a coleção " 
					+ filter.getEntityClass().getSimpleName() + "." + colProperty, e));
		}		
	}

}
