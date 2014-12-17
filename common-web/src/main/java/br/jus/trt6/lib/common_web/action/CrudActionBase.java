package br.jus.trt6.lib.common_web.action;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.DataModel;

import org.primefaces.model.LazyDataModel;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.business.facade.CrudFacade;
import br.jus.trt.lib.common_core.util.DIContainerUtil;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.qbe.util.ReflectionUtil;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt6.lib.common_web.action.pagination.LazyEntityDataModel;
import br.jus.trt6.lib.common_web.action.pagination.LazyEntityProvider;
import javax.annotation.PostConstruct;

/**
 * Classe base para Actions do tipo CRUD, associados a uma entidade de domínio
 * específica. Implementa o comportamento padrão determinado para CRUDs,
 * permitindo a customização de todas as operações.
 * 
 * @author augusto
 * 
 * @param <ENTITY>
 *            Entidade de domínio associada a este Action Crud.
 * @param <PK>
 *            Tipo da chave primária da entidade
 * @param <FACADE>
 *            Manager compatível com a entidade de negócio a ser utilizado.
 */
@SuppressWarnings("serial")
public abstract class CrudActionBase<ENTITY extends Entity<PK>, PK extends Serializable, FACADE extends CrudFacade<ENTITY, PK>>
		extends ActionBase implements LazyEntityProvider<ENTITY> {

	/**
	 * Fachada de serviços para impleentação de um fluxo CRUD.
	 */
	private FACADE facade;

	/**
	 * Exemplo para utilização no formulário de consulta e preenchimento do
	 * filtro de consulta
	 */
	private ENTITY example;

	/**
	 * Lista para armazenar o resultado da consulta. Pode ser uma lista simples
	 * ou paginada.
	 */
	private DataModel<ENTITY> result;

	/**
	 * Tipo da entidade associada. É importante para instanciação de objetos em
	 * algumas operações genéricas.
	 */
	private Class<ENTITY> entityType;

	/**
	 * Tipo do facade associado, para lookup do serviço no container de DI.
	 */
	private Class<FACADE> facadeType;

	/**
	 * Representa a operação sendo executada neste fluxo de CRUD. O valor
	 * default inicial é {@link CrudOperation#SEARCH}
	 */
	private CrudOperation crudOperation = CrudOperation.SEARCH;

	/** Para uso em formulários de manutenção */
	private ENTITY entity;

	@Override
        @PostConstruct
        public void init() {
		log.entry();

		super.init();
		initObjects();
		initEntity();

		log.exit();
	}

	/**
	 * Consulta registros na base de dados de acordo com os dados preenchidos no
	 * filtro.
	 * 
	 * Este método foi projetado para ser utilizado diretamente por um botão a
	 * partir da interface WEB. No entanto, a feramenta de listagem deverá
	 * definir a forma de execução do fluxo de consulta desta funcionalidade.
	 * 
	 * Quando o DataTable utilizado for mais complexo, com paginação no servidor
	 * ativada ({@link LazyDataModel}), ele mesmo controla o fluxo de execução
	 * da consulta, não devendo o botão de pesquisa invocar nenhum action
	 * method, mas apenas re-renderizar o dataTable.
	 * 
	 * Para dataTables mais simples, com funcionamento em memória, o botão deve
	 * iniciar o fluxo de consulta explícitamente, invocando este método como
	 * actionMethod.
	 */
	public void search() {
		log.entry();

		// sempre cria um novo filtro para evitar "lixo" de configurações
		// anteriores
		QBEFilter<ENTITY> searchFilter = createFilter();
		search(searchFilter);

		log.exit();
	}

	@Override
	public ResultData<ENTITY> search(Filter<ENTITY> searchFilter) {
		log.entry();

		preSearch(searchFilter);
		ResultData<ENTITY> resultData = doSearch(searchFilter);
		postSearch(searchFilter);

		return log.exit(resultData);
	}

	/**
	 * Último passo do fluxo de consulta de dados.
	 * 
	 * @param searchFilter
	 *            Filtro utilizado especialmente neste fluxo de consulta.
	 */
	protected void postSearch(Filter<ENTITY> searchFilter) {

	}

	/**
	 * Primeiro passo do fluxo de consulta.
	 * 
	 * @param searchFilter
	 *            Filtro utilizado especialmente neste fluxo de consulta.
	 */
	protected void preSearch(Filter<ENTITY> searchFilter) {
		log.entry();

		// inclui o exemplo no filtro
		searchFilter.setExample(example);

		// permite a configuração dinâmica da consulta
		configSearch(searchFilter);
		
		log.exit();
	}

	/**
	 * Executa, de fato, a operação de consulta, armazenando o resultado em
	 * {@link #result}
	 * 
	 * @param searchFilter
	 *            Filtro para configuração e exeção desta consulta.
	 * @return Dados com o resultado da consulta realizada.
	 */
	protected ResultData<ENTITY> doSearch(Filter<ENTITY> searchFilter) {
		log.entry();

		List<ENTITY> resultList = getFacade().findAllBy(searchFilter);
		getResult().setWrappedData(resultList);

		int count;
		// TODO estudar uma forma de evitar este instanceof
		if (getResult() instanceof LazyDataModel) {
			LazyDataModel<ENTITY> dataModel = (LazyDataModel<ENTITY>) getResult();
			count = getFacade().count(searchFilter).intValue();
			dataModel.setRowCount(count);
		} else {
			count = resultList.size();
		}

		return log.exit(new ResultData<ENTITY>(resultList, count));

	}

	/**
	 * Permite à subclasse configurar os parâmetros desejados para a realização
	 * da consulta.
	 * 
	 * @param filter
	 *            Classe a ser configurada com os parâmetros para realização da
	 *            consulta
	 */
	protected void configSearch(Filter<? extends ENTITY> filter) {
		/*
		 * ponto de extensão para configuração da consulta pela sub-classe. a
		 * implementação default considera a configuração por anotações
		 */
	}

	/**
	 * Inicializa os objetos de controle desclarados.
	 */
	protected void initObjects() {
		log.entry();
		
		initGenericTypes();
		initExample();
		initFacade();
		initDataModel();
		
		log.exit();
	}

	/**
	 * Inicia o tipo de dataModel concreto que deverá ser utilizado neste
	 * action.
	 */
	protected void initDataModel() {
		log.entry();

		this.result = new LazyEntityDataModel<ENTITY>(this);
		
		log.exit();
	}

	/**
	 * Inicializa os tipos genéricos utilizados nesta classe, ENTITY e FACADE.
	 */
	@SuppressWarnings("unchecked")
	protected void initGenericTypes() {
		log.entry();
		
		List<Class<?>> genericsTypedArguments = JavaGenericsUtil
				.getGenericTypedArguments(CrudActionBase.class, this.getClass());

		log.debug("Descobrir tipo da entidade via generics");
		this.entityType = (Class<ENTITY>) genericsTypedArguments.get(0);

		log.debug("Descobrir tipo da fachada via generics");
		this.facadeType = (Class<FACADE>) genericsTypedArguments.get(2);

		log.exit();
	}

	/**
	 * Inicializa o objeto concreto que representa a Facade.
	 */
	protected void initFacade() {
		log.entry();

		this.facade = new DIContainerUtil().lookup(getFacadeType());
		
		log.exit();
	}

	/**
	 * Inicializa o objeto que representa o exemplo do filtro da consulta
	 */
	protected void initExample() {
		log.entry();

		this.example = ReflectionUtil.instantiate(getEntityType());
		
		log.exit();
	}

	/**
	 * Método de fábrica de um {@link Filter}
	 * 
	 * @return Nova instância de {@link Filter}
	 */
	protected QBEFilter<ENTITY> createFilter() {
		log.entry();
		QBEFilter<ENTITY> qbeFilter = new QBEFilter<ENTITY>(getEntityType());
		return log.exit(qbeFilter);
	}

	/**
	 * Inicializa o objeto que representa a entidade associada a este action.
	 */
	protected void initEntity() {
		log.entry();

		this.entity = ReflectionUtil.instantiate(getEntityType());
		
		log.exit();
	}

	/**
	 * Persiste um objeto na base de dados e atualiza a listagem. Permite
	 * operações de INCLUSÃO E ALTERAÇÃO, desde que a chave primária seja gerada
	 * em banco por uma sequence (diferenciando inclusão de alteração).
	 */
	public void save() {
		log.entry();

		preSave();
		doSave();
		postSave();
		
		log.exit();
	}

	/**
	 * Executado após a persistência da entidade. Exibe a mensagem de sucesso,
	 * limpa o formulário, altera a operação, realiza a consulta.
	 */
	protected void postSave() {
		log.entry();

		postPersist();
		
		log.exit();
	}

	/** Precede a persistência da entidade */
	protected void preSave() {
		// nada
	}

	/**
	 * Persiste de fato a entidade, exibindo mensagem de sucesso.
	 */
	protected void doSave() {
		log.entry();

		getFacade().saveAndFlush(getEntity());
		
		log.exit();
	}

	/**
	 * Na configuração básica desta classe, o comportamento após persistência é
	 * sempre igual: Exibe a mensagem de sucesso, limpa o formulário, altera a
	 * operação apara SEARCH.
	 */
	protected void postPersist() {
		log.entry();

		showInfoMessage(getSaveSuccessMessage());
		initEntity();
		setCrudOperation(CrudOperation.SEARCH);
		
		log.exit();
	}

	/**
	 * Insere um objeto na base de dados e atualiza a listagem. Permite apenas a
	 * operação de INCLUSÃO.
	 */
	public void insert() {
		log.entry();

		preInsert();
		doInsert();
		postUpdate();
		
		log.exit();
	}

	/**
	 * Operações pós-insert. Exibe a mensagem de sucesso, limpa o formulário,
	 * altera a operação, realiza a consulta.
	 */
	protected void postInsert() {
		log.entry();

		showInfoMessage(getSaveSuccessMessage());
		postPersist();
		
		log.exit();
	}

	/**
	 * Realiza de fato a operação insert.
	 */
	protected void doInsert() {
		log.entry();

		getFacade().saveAndFlush(getEntity());
		
		log.exit();
	}

	/**
	 * Precede a operacao doInsert.
	 */
	protected void preInsert() {
		// nada
	}

	/**
	 * Atualiza um objeto na base de dados e atualiza a listagem. Permite apenas
	 * a operação de ALTERAÇÂO/ATUALIZAÇÃO.
	 */
	public void update() {
		log.entry();

		preUpdate();
		doUpdate();
		postUpdate();
		
		log.exit();
	}

	/**
	 * Operações pós-update. Exibe a mensagem de sucesso, limpa o formulário,
	 * altera a operação, realiza a consulta.
	 */
	protected void postUpdate() {
		log.entry();

		postPersist();
		
		log.exit();
	}

	/**
	 * Realiza de fato o update
	 */
	protected void doUpdate() {
		log.entry();

		getFacade().saveAndFlush(getEntity());
		
		log.exit();
	}

	/**
	 * Precede a operação de update.
	 */
	protected void preUpdate() {
		// nada
	}

	/**
	 * Exclui um registro da base de dados e atualiza a listagem.
	 * 
	 * @param entidade
	 *            Registro para exclusão
	 */
	public void delete(ENTITY entidade) {
		log.entry();

		preDelete(entidade);
		doDelete(entidade);
		postDelete(entidade);
		
		log.exit();
	}

	/**
	 * Operações pós-update. Exibe a mensagem de sucesso, limpa o formulário,
	 * altera a operação para SEARCH.
	 */
	protected void postDelete(ENTITY entidade) {
		log.entry();

		showInfoMessage(getRemoveSuccessMessage());
		setCrudOperation(CrudOperation.SEARCH);
		
		log.exit();
	}

	/**
	 * Delete de fato o registro.
	 * 
	 * @param entidade
	 *            Registro para exclusão.
	 */
	protected void doDelete(ENTITY entidade) {
		log.entry();

		getFacade().removeAndFlush(entidade);
		
		log.exit();
	}

	/**
	 * Precede a operação delete.
	 * 
	 * @param entidade
	 *            Registro para exclusão.
	 */
	protected void preDelete(ENTITY entidade) {
		// nada
	}

	/**
	 * Carrega os dados de um registro para alteração.
	 * 
	 * @param entidade
	 *            Objeto com os dados para alteração.
	 */
	public void prepareToEdit(ENTITY entidade) {
		log.entry();

		preLoad(entidade);
		doLoad(entidade);
		postLoad();
		
		log.exit();
	}

	/**
	 * Operações pós-carregamento. Configuração a operação seguinte.
	 */
	protected void postLoad() {
		log.entry();

		setCrudOperation(CrudOperation.EDIT);
		
		log.exit();
	}

	/**
	 * Realiza de fato a preparação para edição de uma entidade.
	 * 
	 * @param entidade
	 *            Entidade para preparação.
	 */
	protected void doLoad(ENTITY entidade) {
		log.entry();

		// cria um filtro para realizar o carregamento e permitir outras
		// configurações
		Filter<ENTITY> loadFilter = createFilter();
		configLoad(entidade, loadFilter);

		setEntity(getFacade().findBy(loadFilter));
		
		log.exit();
	}

	/**
	 * Método para configuração do carregamento de uma entidade para edição.
	 * Esta implementação default considera apenas o ID da entidade para
	 * carregamento, deixando o desenvolvedor à vontade para incluir outras
	 * configurações.
	 * 
	 * @param entidade
	 *            Entidade para carregamento.
	 * @param loadFilter
	 *            Filtro a ser utilizado no carregamento.
	 */
	protected void configLoad(ENTITY entidade, Filter<ENTITY> loadFilter) {
		log.entry();

		loadFilter.filterBy("id", Operators.equal(), entidade.getId());
		
		log.exit();
	}

	/**
	 * Precede o carregamendo de uma entidade para alteração
	 * 
	 * @param entidade
	 *            Entidade para preparação para alteração
	 */
	protected void preLoad(ENTITY entidade) {
		// nada
	}

	/**
	 * Prepara o action para inclusão de um novo registro
	 */
	public void prepareToInsert() {
		log.entry();

		prePrepareToInsert();
		doPrepareToInsert();
		postPrepareToInsert();
		
		log.exit();
	}

	/**
	 * Operações pós-preparacao.
	 */
	protected void postPrepareToInsert() {
		log.entry();

		setCrudOperation(CrudOperation.EDIT);
		
		log.exit();
	}

	/**
	 * Instancia novamente a entidade para permitir o cadastro de um novo
	 * registro
	 */
	protected void doPrepareToInsert() {
		log.entry();

		initEntity(); // garante que há uma nova entidade para o formulário
		
		log.exit();
	}

	/**
	 * Precede o prepareToInsert()
	 */
	protected void prePrepareToInsert() {
		// nada
	}

	/**
	 * Cancela o andamento da operação corrente, direcionando o fluxo para a
	 * página de listagem.
	 */
	public void cancelOperation() {
		log.entry();

		initEntity();
		setCrudOperation(CrudOperation.SEARCH);
		
		log.exit();
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

	protected FACADE getFacade() {
		return facade;
	}

	protected void setFacade(FACADE facade) {
		this.facade = facade;
	}

	public ENTITY getExample() {
		return example;
	}

	protected void setExample(ENTITY example) {
		this.example = example;
	}

	public DataModel<ENTITY> getResult() {
		return result;
	}

	public void setResult(DataModel<ENTITY> result) {
		this.result = result;
	}

	@SuppressWarnings("unchecked")
	public List<ENTITY> getResultList() {
		return (List<ENTITY>) getResult().getWrappedData();
	}

	@Override
	public Class<ENTITY> getEntityType() {
		return entityType;
	}

	protected Class<FACADE> getFacadeType() {
		return facadeType;
	}

}
