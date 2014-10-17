package br.jus.trt6.lib.common_web.action;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.business.facade.CrudFacade;
import br.jus.trt.lib.common_core.util.DIContainerUtil;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.common_core.util.ReflectionUtil;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.operator.Operators;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import org.apache.logging.log4j.Logger;

/**
 * Classe base para Actions do tipo CRUD, associados a uma entidade de domínio específica. Implementa o comportamento padrão
 * determinado para CRUDs, permitindo a customização de todas as operações.
 * @author augusto
 * 
 * @param <ENTITY> Entidade de domínio associada a este Action Crud.
 * @param <PK> Tipo da chave primária da entidade
 * @param <FACADE> Manager compatível com a entidade de negócio a ser utilizado.
 */
@SuppressWarnings("serial")
public abstract class CrudActionBase<ENTITY extends Entity<PK>, PK extends Serializable, FACADE extends CrudFacade<ENTITY, PK>> extends ActionBase {

    @Inject 
    private Logger log;
    
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

	/**
	 * Representa a operação sendo executada neste fluxo de CRUD
	 */
	private CrudOperation crudOperation = CrudOperation.SEARCH; // inicia em modo Consulta
	
	
	/** Para uso em formulários de manutenção */
	private ENTITY entity;
	
	@Override
	public void init() {
		super.init();
                log.entry();
		initObjects();
		initEntity();
	}
	
	/**
	 * Consulta registros na base de dados de acordo com os dados preenchidos no filtro.
	 */
	public void search() {
            log.entry();
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
            log.entry();
		List<ENTITY> result = getFacade().findAllBy(filter);
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
            log.entry();
	}
	
	/**
	 * Inicializa os objetos de controle desclarados.
	 */
	protected void initObjects() {
            log.entry();
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
            log.entry();
		List<Class<?>> genericsTypedArguments = JavaGenericsUtil.getGenericTypedArguments(CrudActionBase.class, this.getClass());
                log.debug("Descobrir tipo da entidade via generics");
		this.entityType = (Class<ENTITY>) genericsTypedArguments.get(0); // a entidade é o primeiro tipo declarado no action
                log.debug("Descobrir tipo da fachada via generics");
		this.facadeType = (Class<FACADE>) genericsTypedArguments.get(2); // a facade é o segundo tipo declarado.
	}

	/**
	 * Inicializa o objeto concreto que representa a Facade.
	 */
	protected void initFacade() {
            log.entry();
		this.facade = new DIContainerUtil().lookup(getFacadeType());
	}

	/**
	 * Inicializa o objeto que representa o exemplo do filtro da consulta
	 */
	protected void initExample() {
            log.entry();
		this.example = ReflectionUtil.instantiate(getEntityType());
	}

	/**
	 * Inicializa o objeto Filtro para ser utilizar na busca com filtro.
	 */
	protected void initFilter() {
            log.entry();
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

	/**
	 * Inicializa o objeto que representa a entidade associada a este action.
	 */
	protected void initEntity() {
            log.entry();
		this.entity = ReflectionUtil.instantiate(getEntityType());
	}
	
	/**
	 * Persiste um objeto na base de dados e atualiza a listagem. 
	 * Permite operações de INCLUSÃO E ALTERAÇÃO, desde que a chave primária seja gerada em banco por uma sequence (diferenciando inclusão de alteração).
	 */
	public void save() {
		preSave();
		doSave();
		postSave();
	}

	/**
	 * Executado após a persistência da entidade. Exibe a mensagem de sucesso, limpa o formulário, altera a operação, realiza a consulta.
	 */
	protected void postSave() {
		postPersist();
	}
	
	/** Precede a persistência da entidade */
	protected void preSave() {
		// nada
	}

	/**
	 * Persiste de fato a entidade, exibindo mensagem de sucesso.
	 */
	protected void doSave() {
		getFacade().saveAndFlush(getEntity());
	}
	
	/**
	 * Na configuração básica desta classe, o comportamento após persistência 
	 * é sempre igual: Exibe a mensagem de sucesso, limpa o formulário, altera a operação, realiza a consulta.
	 */
	private void postPersist() {
		showInfoMessage(getSaveSuccessMessage());
		initEntity();
		setCrudOperation(CrudOperation.SEARCH);
		search();
	}
	
	/**
	 * Insere um objeto na base de dados e atualiza a listagem. 
	 * Permite apenas a operação de INCLUSÃO.
	 */
	public void insert() {
		preInsert();
		doInsert();
		postUpdate();
	}

	/**
	 * Operações pós-insert. Exibe a mensagem de sucesso, limpa o formulário, altera a operação, realiza a consulta.
	 */
	protected void postInsert() {
		showInfoMessage(getSaveSuccessMessage());
		postPersist();
	}

	/**
	 * Realiza de fato a operação insert.
	 */
	protected void doInsert() {
		getFacade().saveAndFlush(getEntity());
	}
	
	/**
	 * Precede a operacao doInsert.
	 */
	protected void preInsert() {
		// nada
	}

	/**
	 * Atualiza um objeto na base de dados e atualiza a listagem. 
	 * Permite apenas a operação de ALTERAÇÂO/ATUALIZAÇÃO.
	 */
	public void update() {
		preUpdate();
		doUpdate();
		postUpdate();
	}

	/**
	 * Operações pós-update. Exibe a mensagem de sucesso, limpa o formulário, altera a operação, realiza a consulta.
	 */
	protected void postUpdate() {
		postPersist();
	}

	/**
	 * Realiza de fato o update
	 */
	protected void doUpdate() {
		getFacade().saveAndFlush(getEntity());
	}

	/**
	 * Precede a operação de update.
	 */
	protected void preUpdate() {
		// nada
	}

	/**
	 * Exclui um registro da base de dados e atualiza a listagem. 
	 * @param entidade Registro para exclusão
	 */
	public void delete(ENTITY entidade) {
		preDelete(entidade);
		doDelete(entidade);
		postDelete(entidade);
	}

	/**
	 * Operações pós-update. Exibe a mensagem de sucesso, limpa o formulário, altera a operação, realiza a consulta.
	 */
	protected void postDelete(ENTITY entidade) {
		showInfoMessage(getRemoveSuccessMessage());
		setCrudOperation(CrudOperation.SEARCH);
		search();
	}

	/**
	 * Delete de fato o registro.
	 * @param entidade Registro para exclusão.
	 */
	protected void doDelete(ENTITY entidade) {
		getFacade().removeAndFlush(entidade);
	}
	
	/**
	 * Precede a operação delete.
	 * @param entidade Registro para exclusão.
	 */
	protected void preDelete(ENTITY entidade) {
		// nada
	}

	/**
	 * Carrega os dados de um registro para alteração.
	 * @param entidade Objeto com os dados para alteração.
	 */
	public void prepareToEdit(ENTITY entidade) {
		preLoad(entidade);
		doLoad(entidade);
		postLoad();
	}

	/**
	 * Operações pós-carregamento. Configuração a operação seguinte.
	 */
	protected void postLoad() {
		setCrudOperation(CrudOperation.EDIT);
	}

	/**
	 * Realiza de fato a preparação para edição de uma entidade.
	 * @param entidade Entidade para preparação.
	 */
	protected void doLoad(ENTITY entidade) {
		// cria um filtro para realizar o carregamento e permitir outras configurações
		Filter<ENTITY> loadFilter = createFilter();
		configLoad(entidade, loadFilter);
		
		
		setEntity(getFacade().findBy(loadFilter));
	}

	/**
	 * Método para configuração do carregamento de uma entidade para edição. Esta implementação
	 * default considera apenas o ID da entidade para carregamento, deixando o desenvolvedor à vontade para incluir outras configurações.
	 * @param entidade Entidade para carregamento.
	 * @param loadFilter Filtro a ser utilizado no carregamento.
	 */
	protected void configLoad(ENTITY entidade, Filter<ENTITY> loadFilter) {
		loadFilter.filterBy("id", Operators.equal(), entidade.getId());
	}
	
	/**
	 * Precede o carregamendo de uma entidade para alteração
	 * @param entidade Entidade para preparação para alteração
	 */
	protected void preLoad(ENTITY entidade) {
		// nada
	}
	
	/**
	 * Prepara o action para inclusão de um novo registro
	 */
	public void prepareToInsert() {
		prePrepareToInsert();
		doPrepareToInsert();
		postPrepareToInsert();
	}

	/**
	 * Operações pós-preparacao. 
	 */
	protected void postPrepareToInsert() {
		setCrudOperation(CrudOperation.EDIT);
	}

	/**
	 * Instancia novamente a entidade para permitir o cadastro de um novo registro
	 */
	protected void doPrepareToInsert() {
		initEntity(); // garante que há uma nova entidade para o formulário
	}
	
	/**
	 * Precede o prepareToInsert()
	 */
	protected void prePrepareToInsert() {
		// nada
	}

	/**
	 * Cancela o andamento da operação corrente, direcionando o fluxo
	 * para a página de listagem.
	 */
	public void cancelOperation() {
		initEntity();
            setCrudOperation(CrudOperation.SEARCH);
	}
	
	/**
	 * @return Mensagem a ser exibida após a operação "excluir".
	 */
	protected String getRemoveSuccessMessage() {
		return "Registro excluído com sucesso.";
	}
	
	/**
	 * @return Mensagem a ser exibida após a operação "salvar".
	 */
	protected String getSaveSuccessMessage() {
		return "Operação realizada com sucesso.";
	}

	public ENTITY getEntity() {
		return entity;
	}

	protected void setEntity(ENTITY entity) {
		this.entity = entity;
	}

	public CrudOperation getCrudOperation() {
		return crudOperation;
	}

	protected void setCrudOperation(CrudOperation operacao) {
		this.crudOperation = operacao;
	}
	
	
}
