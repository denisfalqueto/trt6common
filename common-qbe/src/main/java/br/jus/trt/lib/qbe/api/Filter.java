package br.jus.trt.lib.qbe.api;

import java.util.List;

import br.jus.trt.lib.qbe.api.OperationContainer.ContainerType;

import com.mysema.query.types.Path;

/**
 * Filtro para configuração das operações de consultas QBE.
 * @author augusto
 *
 * @param <TIPO>
 */
public interface Filter<TIPO extends Identifiable> extends Cloneable {

	/**
	 * Permite configurar odernações para a consulta.
	 * @param sortings Configurações de ordenação na ordem em que deve sem aplicadas.
	 * @return this
	 */
	public abstract Filter<TIPO> sortBy(SortConfig... sortings);

	/**
	 * Permite configurar odernações para a consulta.
	 * @param sortings Configurações de ordenação na ordem em que deve sem aplicadas.
	 * @param priority Determina se as propriedades de ordenação devem receber prioridade, sendo posicionadas antes das 
	 * propriedades atualmente configuradas.
	 * @return this
	 */
	public abstract Filter<TIPO> sortBy(boolean priority, SortConfig... sortings);

	/**
	 * Adicionar uma ordenação ascendente para a propriedade.
	 * @param properties Propriedade para ordenação.
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	public abstract Filter<TIPO> sortAscBy(Path... properties);

	/**
	 * Adicionar uma ordenação descendente para a propriedade.
	 * @param properties Propriedade para ordenação.
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	public abstract Filter<TIPO> sortDescBy(Path... properties);

	/**
	 * Remove as ocorrências de ordenação para as propriedades informadas.
	 * @param properties Lista de propriedades para remoção de ocorrências de ordenação
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	public abstract Filter<TIPO> clearSortings(Path... properties);

	/**
	 * Configura uma paginação para a consulta a ser realizada.
	 * @param firstRegistryIndex Posição do primeiro registro a ser considerado no resultado da consulta. Considere o primeiro registro
	 * como sendo a posição 0 (zero).
	 * @param maxNumberRegistries Número máximo de registros a serem considerados do resultado da consulta.
	 * @return this 
	 */
	public abstract Filter<TIPO> paginate(Integer firstRegistryIndex, Integer maxNumberRegistries);

	/**
	 * Adiciona operações ao container raiz desta consulta
	 * @param operations Operações.
	 * @return this 
	 */
	public abstract Filter<TIPO> filterBy(Operation... operations);

	/**
	 * Adiciona uma operação com as características informadas. O valor
	 * a ser aplicado no filtro da consulta deverá ser o informado no objeto exemplo. Caso o
	 * objeto exemplo não tenha nenhum valor informado para o atributo especificado, esta operação
	 * deverá ser descartada.
	 * @param operador Operador a ser aplicado.
	 * @param property Propriedade a ser considerada.
	 * @return this 
	 */
	@SuppressWarnings("rawtypes")
	public abstract Filter<TIPO> filterBy(Path property, Operator<?> operador);

	/**
	 * Adiciona uma operação com as características informadas. O valor
	 * a ser aplicado na consulta deverá ser, prioritariamente, o valor informado nesta operação,
	 * desconsiderando o valor informado no objeto exemplo..
	 * @param operator Operador a ser aplicado.
	 * @param property Propriedade a ser considerada.
	 * @param values Valores a serem aplicados na operação. Estes valores têm
	 * prioridade sobre o valor encontrado no objeto exemplo.
	 * @return this 
	 */
	@SuppressWarnings("rawtypes")
	public abstract Filter<TIPO> filterBy(Path property, Operator<?> operator, Object... values);

	/**
	 * Adiciona um subcontainer ao container raiz deste filtro.
	 * @param container Container a ser adicionar como subcontainer.
	 * @return this 
	 */
	public abstract Filter<TIPO> addContainerOperation(OperationContainer container);

	/**
	 * Cria e adiciona um subcontainer de operações OR no container raiz deste filtro.
	 * @param operations Operações que deverão fazer parte do subcontainer.
	 * @return Referência para o subcontainer criado.
	 */
	public abstract OperationContainer addOr(Operation... operations);

	/**
	 * Cria e adiciona um subcontainer de operações AND no container raiz deste filtro.
	 * @param operations Operações que deverão fazer parte do subcontainer.
	 * @return Referência para o subcontainer criado.
	 */
	public abstract OperationContainer addAnd(Operation... operations);

	/**
	 * Adiciona configurações de carregamento de dependências a este filtro.
	 * @param fetches Configurações de carregamento de dependências.
	 * @return this
	 */
	public abstract Filter<TIPO> addFetch(FetchMode... fetches);

	/**
	 * Adiciona configurações de carregamento de dependências a este filtro. Utiliza
	 * {@link FetchMode} com {@link JoinType#LEFT} por default
	 * @param properties Propriedade que deverá ser configurada para carregamento rápido.
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	public abstract Filter<TIPO> addFetch(Path... properties);

	/**
	 * Incrementa o nível da consulta em 1, fazendo com que o analizador do objeto exemplo
	 * navegue um nível mais profundo, considerando também os atributos de objetos associados ao exemplo. 
	 * @return this 
	 */
	public abstract Filter<TIPO> incrementProspectionLevel();

	/**
	 * Configura o tipo do container principal deste filtro.
	 * @param tipo Tipo de container.
	 * @return this
	 */
	public abstract Filter<TIPO> setRootContainerType(ContainerType tipo);

	/* get e set */
	public abstract Class<? extends TIPO> getEntityClass();

	public abstract TIPO getExample();

	public abstract void setExample(TIPO exemplo);

	public abstract Pagination getPagination();

	public abstract void setPagination(Pagination paginacao);

	public abstract List<SortConfig> getSortings();

	public abstract int getProspectionLevel();

	public abstract void setProspectionLevel(int nivel);

	public abstract OperationContainer getRootContainer();

	public abstract Operator<?> getStringDefaultOperator();

	public abstract void setStringDefaultOperator(Operator<?> operadorPadraoString);

	public abstract List<FetchMode> getFetches();

	/**
	 * @return true se este filtro está configurado para operar com paginação
	 * na base de dados.
	 */
	public abstract boolean isPaged();

	/**
	 * Permite a configuração do tipo da entidade mapeada que deverá estar associada a esta consulta.
	 * na prática, esta entidade será utilizada na cláusula FROM quando a consulta for renderizada.
	 * 
	 * Com este recurso, é possível utilizar tipos diferentes nas propriedades entityClass e example,
	 * porém é importante perceber que o tipo do objeto em "example" deve ser descedente de entityClass. 
	 * Esta situação é mais comum quando ocorre hierarquia de entidades, e a consulta será realizada sobre 
	 * uma entidade mais alta na hierarquia - sendo esta abstrata, porém os dados da consulta serão extraídos de um objeto
	 * exemplo de um tipo mais baixo na hierarquia. Ex: entityClass=Compentencia, exemplo=CompetenciaTecnica, onde
	 * Competencia extends CompetenciaTecnica.
	 * @param entityClass Tipo da entidade sobre a qual a consulta deverá ser executada.
	 */
	public abstract void setEntityClass(Class<? extends TIPO> entityClass);

	// FIXME Este conceito deve ser movido para dentro do pacote de Criteria
	public abstract boolean isForcePreFetchCollection();

}