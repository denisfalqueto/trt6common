package br.jus.trt6.lib.common_web.action.pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.SortConfig;
import br.jus.trt.lib.qbe.api.SortConfig.SortType;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt6.lib.common_web.action.ResultData;

/**
 * Data Model para paginação no servidor de consultas em Actions.
 * 
 * @author augusto
 * @param <ENTITY>
 *            Entidade assiada a este DataModel.
 */
public class LazyEntityDataModel<ENTITY extends Entity<?>> extends
		LazyDataModel<ENTITY> {

	private static final long serialVersionUID = 1L;

	private Logger log = LogManager.getLogger();
	
	/**
	 * Provê a acesso à infraestrutura necessária para realizar uma consulta
	 * dinâmica
	 */
	private LazyEntityProvider<ENTITY> entityProvider;

	/**
	 * @param entityProvider
	 *            Provê a acesso à infraestrutura necessária para realizar uma
	 *            consulta dinâmica
	 */
	public LazyEntityDataModel(LazyEntityProvider<ENTITY> entityProvider) {
		super();
		this.entityProvider = entityProvider;
	}

	@Override
	public List<ENTITY> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		
		SortMeta sortMeta = new SortMeta(null, sortField, sortOrder, null);
		ArrayList<SortMeta> multiSortMeta = new ArrayList<SortMeta>();
		multiSortMeta.add(sortMeta);
		
		return load(first, pageSize, multiSortMeta, filters);
	}
	
	/**
	 * Realiza a consulta paginada.
	 */
	@Override
	public List<ENTITY> load(int first, int pageSize,
			List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		
		/*
		 * Este método é utilizado pelo p:dataTable quando a ordenação múltipla
		 * é ativada (sortMode="multiple"). No entanto, o dataTable sempre envia uma
		 * lista com apenas um elemento, a última coluna clicada para ordenação. Isto
		 * resulta em um comportamento igual à ordenação simples (sortMode="simple").
		 * Este comportamento parece ser um bug no conjunto p:dataTable = LazyDataModel.
		 * Talvez haja seja possível implementar uma solução para este problema, implementando
		 * um cache de colunas clicadas para ordenação. 
		 * 
		 * TODO:Avaliar se vale a pena implementar ordenação múltipla!
		 */
		
		
		
		log.entry(first, pageSize, multiSortMeta, filters);
		
		log.debug("Executando a consulta paginada");
		
		/*
		 * Criando filtro genérico com os dados básicos para a paginação. Estes
		 * dados são reconfigurados cada vez que o DataTable sofre alguma operação
		 * (navegação, ordenação, filtro, etc)
		 */
		QBEFilter<ENTITY> filter = new QBEFilter<ENTITY>(entityProvider.getEntityType());
		
		log.debug("configurando página da ordenação");
		filter.paginate(first, pageSize);
		
		if (multiSortMeta != null && !multiSortMeta.isEmpty()) {
			for (SortMeta sortMeta : multiSortMeta) {
				
				// considera a ordenação apenas se for diferente de UNSORTED
				if (!SortOrder.UNSORTED.equals(sortMeta.getSortOrder())) {
					log.debug("Ordenação é diferente de unsorted e será adicionada");
					filter.sortBy(new SortConfig(sortMeta.getSortField(), asSortType(sortMeta.getSortOrder())));
				}	
			}
		}
		
		if (filters != null && !filters.isEmpty()) {
			for (Entry<String, Object> filterEntry : filters.entrySet()) {
				log.debug("Adicioando filtro do dataTable");
				filter.filterBy(filterEntry.getKey(), Operators.like(false), filterEntry.getValue());
			}
		}

		log.debug("Realizando a a consulta dinâmica utilizando o filtro mínimo configurado");
		ResultData<ENTITY> resultData = entityProvider.search(filter);
		
		// atualizando resultado da consulta no DataModel
		List<ENTITY> resultList = resultData.getResultList();
		setRowCount(resultData.getCountAll().intValue());

		return log.exit(resultList);
	}

	/**
	 * Transforma um {@link SortOrder} em um {@link SortType} compatível.
	 * @return {@link SortType} compatível.
	 */
	protected SortType asSortType(SortOrder sortOrder) {
		return SortOrder.DESCENDING.equals(sortOrder) ? SortType.DESCENDING : SortType.ASCENDING;
	}

}
