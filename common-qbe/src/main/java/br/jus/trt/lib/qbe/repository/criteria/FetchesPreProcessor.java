package br.jus.trt.lib.qbe.repository.criteria;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;

import br.jus.trt.lib.qbe.api.FetchMode;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.exception.QbeException;

/**
 * Realiza o processamento dos fetches configurados, exceto para coleções que serão tratadas com consultas independentes.
 * @author augusto
 */
public class FetchesPreProcessor extends FetchesProcessor {

	public FetchesPreProcessor(Criteria criteria, Filter<?> filter, Map<String, String> aliasCache) {
		super(criteria, filter, aliasCache);
	}

	public void process() {
		if (getFilter().getFetches() != null) {
			
			List<FetchMode> splittedFetch = splitFetchProperties();
			for (FetchMode fetch : splittedFetch) {
				/*
				 * Cria alias para todas as propriedades. Para o caso de coleções, cria alias apenas para a primeira coleção
				 * descartando as demais
				 */
				try {
					boolean isCollection = hasCollection(fetch);
					if (!isCollection || getFilter().isForcePreFetchCollection()) {
						AliasUtil.createFetchAlias(getAliasCache(), getCriteria(), fetch.getProperty(), getJoinFetch(fetch));
					} 
				} catch (Exception e) {
					throw new QbeException("QBE: Falha ao tentar processar FETCH para a propridade " + fetch.getProperty(), e);
				}
			}
		}
	}

	protected org.hibernate.sql.JoinType getJoinFetch(FetchMode fetch) {
		return new JoinTypeParser().parse(fetch.getJoinType());
	}

}