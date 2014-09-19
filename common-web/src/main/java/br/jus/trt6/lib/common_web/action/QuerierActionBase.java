package br.jus.trt6.lib.common_web.action;

import java.util.List;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.business.facade.QuerierFacade;
import br.jus.trt.lib.common_core.util.DIContainerUtil;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.common_core.util.ReflectionUtil;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.Filter;

/**
 * Action com customizações específicas para execução de operações de consulta sobre uma entidade de domínio.
 *
 * @param <ENTITY> Entidade de domínio associada a este Action Crud.
 * @param <FACADE> Fachada compatível com a entidade de negócio a ser utilizada, disponibilizando operações 
 * de consulta de dados.
 * 
 * @author augusto
 */
@SuppressWarnings("serial")
public abstract class QuerierActionBase<ENTITY extends Entity<?>, FACADE extends QuerierFacade<ENTITY>> extends ActionBase {

	/**
	 * Fachada de serviços para impleentação de um fluxo CRUD.
	 */
	private FACADE facade;

	/** Filtro para configurações das consultas a serem realizadas */
	private Filter<ENTITY> filter;
	
	/** Exemplo para utilização no formulário de consulta e preenchimento do filtro de consulta */
	private ENTITY example;
	
	/** Lista para armazenar o resultado da consulta. Pode ser uma lista simples ou paginada. */
	private List<ENTITY> resultList;
	
	/**
	 * Tipo da entidade associada. É importante para instanciação de objetos em algumas operações genéricas.
	 */
	private Class<ENTITY> entityType;
	
	/**
	 * Tipo do facade associado, para lookup do serviço no container de DI.
	 */
	private Class<FACADE> facadeType;

	@Override
	public void init() {
		super.init();
		initObjects();
	}
	
	/**
	 * Consulta registros na base de dados de acordo com os dados preenchidos no filtro.
	 */
	public void search() {
		// sempre cria um novo filtro para evitar "lixo" de configurações anteriores
		initFilter();

		// inclui o exemplo no filtro
		getFilter().setExample(example);
		
		// permite a configuração dinâmica da consulta
		configSearch(getFilter());

		doSearch();
	}
	
	/**
	 * Executa, de fato, a operação de consulta, armazenando o resultado em {@link QuerierAction#entityList} 
	 */
	protected void doSearch() {
		List<ENTITY> result = getFacade().search(filter);
		setResultList(result);
	}
	
	/**
	 * Permite à subclasse configurar os parâmetros desejados para a realização da consulta.
	 * @param filter Classe a ser configurada com os parâmetros para realização da consulta
	 */
	protected void configSearch(Filter<? extends ENTITY> filter) {
		/*
		 * ponto de extensão para configuração da consulta pela sub-classe.
		 * a implementação default considera a configuração por anotações
		 */
	}
	
	/**
	 * Inicializa os objetos de controle desclarados.
	 */
	protected void initObjects() {
		initGenericTypes();
		initExample();
		initFilter();
		initFacade();
	}
	
	/**
	 * Inicializa os tipos genéricos utilizados nesta classe, ENTITY e FACADE.
	 */
	@SuppressWarnings("unchecked")
	protected void initGenericTypes() {
		List<Class<?>> genericsTypedArguments = JavaGenericsUtil.getGenericTypedArguments(QuerierActionBase.class, this.getClass());
		this.entityType = (Class<ENTITY>) genericsTypedArguments.get(0); // a entidade é o primeiro tipo declarado no action
		this.facadeType = (Class<FACADE>) genericsTypedArguments.get(1); // a facade é o segundo tipo declarado.
	}

	/**
	 * Inicializa o objeto concreto que representa a Facade.
	 */
	protected void initFacade() {
		this.facade = new DIContainerUtil().lookup(getFacadeType());
	}

	/**
	 * Inicializa o objeto que representa o exemplo do filtro da consulta
	 */
	protected void initExample() {
		this.example = ReflectionUtil.instantiate(getEntityType());
	}

	/**
	 * Inicializa o objeto Filtro para ser utilizar na busca com filtro.
	 */
	protected void initFilter() {
		filter = createFilter();
	}

	/**
	 * Método de fábrica de um {@link Filter}
	 * @return Nova instância de {@link Filter}
	 */
	protected QBEFilter<ENTITY> createFilter() {
		return new QBEFilter<ENTITY>(getEntityType());
	}
	
	protected FACADE getFacade() {
		return facade;
	}

	protected void setFacade(FACADE facade) {
		this.facade = facade;
	}

	public Filter<ENTITY> getFilter() {
		return filter;
	}

	protected void setFilter(Filter<ENTITY> filter) {
		this.filter = filter;
	}

	public ENTITY getExample() {
		return example;
	}

	protected void setExample(ENTITY example) {
		this.example = example;
	}

	public List<ENTITY> getResultList() {
		return resultList;
	}

	protected void setResultList(List<ENTITY> entityList) {
		this.resultList = entityList;
	}

	protected Class<ENTITY> getEntityType() {
		return entityType;
	}

	protected Class<FACADE> getFacadeType() {
		return facadeType;
	}

}
