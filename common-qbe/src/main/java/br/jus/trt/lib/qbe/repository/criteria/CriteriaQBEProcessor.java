package br.jus.trt.lib.qbe.repository.criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.jus.trt.lib.qbe.Property;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.Identifiable;
import br.jus.trt.lib.qbe.api.Operation;
import br.jus.trt.lib.qbe.api.OperationContainer;
import br.jus.trt.lib.qbe.api.OperationContainer.ContainerType;
import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.api.Pagination;
import br.jus.trt.lib.qbe.api.SortConfig;
import br.jus.trt.lib.qbe.api.exception.QbeException;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.util.ReflectionUtil;

/**
 * Responsável por realizar todo o processamento das operações de QBE com critéria.
 * @author augusto
 */
public class CriteriaQBEProcessor <TIPO extends Identifiable> {

	private Filter<? extends TIPO> filter;
	private Criteria criteria;
	private Session session;
	private OperatorProcessorRepository operatorProcessorRepository;
	
	/** Cache para evitar a criação duplicada de alias no Criteria */
	private Map<String, String> aliasCache;
	
	/**
	 * @param processorRepository Repositório de processadores dos operadores suportados no QBE com Criteria.
	 * @param session Session válida para acesso à base de dados.
	 * @param filter Filtro contendo todas as configurações necessárias para criação do QBE.
	 */
	public CriteriaQBEProcessor(OperatorProcessorRepository processorRepository, Session session, Filter<? extends TIPO> filter) {
		this.operatorProcessorRepository = processorRepository;
		this.filter = filter;
		this.session = session;
		this.criteria = initializeCriteria();
		this.aliasCache = new HashMap<String, String>();
	}

	/**
	 * Realiza consulta dinâmica a partir da análie das propriedades do filtro configurado.
	 * @return Resultado da consulta de acordo com os parâmetros de entrada.
	 */
	public List<TIPO> search(){
		try {
			processCriteria();
			
			applyFetches();
			applyOrderings(filter.getSortings());
			applyPaging(filter.getPagination());
			
			// distinct
			//criteria.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
			
			@SuppressWarnings("unchecked")
			List<TIPO> list = criteria.list();
			
			applyManualFetches(list);
			
			
			return list;
		} catch (Throwable e) {
			throw getDBException(e);
		}

	}

	protected void applyManualFetches(List<TIPO> list) {
		new FetchesManualProcessor(getOperatorProcessorRepository(), session, list, criteria, filter, aliasCache).process();
	}

	protected void applyFetches() {
		new FetchesPreProcessor(criteria, filter, aliasCache).process();
	}
	
	/**
	 * Realiza consulta dinâmica a partir da análie das propriedades do filtro configuradi.
	 * @return Total de registros encontrados de acordo com o filtro informado.
	 */
	public long count(){
		try {
			processCriteria();
			Number count = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();
			return count.longValue();
		} catch (Exception e) {
			throw getDBException(e);
		}

	}
	
	private QbeException getDBException(Throwable e) {
		return new QbeException("Erro ao processar consulta QBE.", e);
	}

	/**
	 * Criar um Criteria a partir das configurações do objeto de consulta.
	 * @param filter Objeto preparado com dados de configuração para a geração dinâmica de uma consulta.
	 * @return Criteria devidamente configurado com os filtros da consulta
	 * @throws Exception 
	 */
	protected void processCriteria() throws Exception {
		OperationContainer containerValido = buildOperations();
		applyOperations(containerValido);
	}

	private Criteria initializeCriteria() {
		return session.createCriteria(filter.getEntityClass());
	}
	
	/**
	 * Extrai do container e de seus subcontainers todas as informações relacionadas às operações configuradas.
	 * @return Container configurado com as operações válidas
	 * @throws Exception
	 */
	protected OperationContainer buildOperations() throws Exception {

		
		// cache para priorizar propriedades em operações customizadas,
		// são aquelas que foram configuradas programaticamente através do método filtro.addOperacao, 
		// e não apenas inferidas do objeto exemplo
		Set<String> cachePropriedadesCustomizadas = new HashSet<String>();		

		OperationContainer containerDefinitivo = extractValidOperations(cachePropriedadesCustomizadas, filter.getExample(), filter.getRootContainer());
		
		// descobrindo operações preenchidas no exemplo e adicionando no container raiz
		List<Property> propriedades = PropertyInspector.extractFilledProperties(filter);
		for (Property propriedade : propriedades) {
			// considera apenas aquelas que não foram diretamente customizadas
			if (!cachePropriedadesCustomizadas.contains(propriedade.generateDotNotation())) {

				// considera operador padrão para Strings configurado no fitro
				Operator<?> operadorDefault = propriedade.getValue() instanceof String 
						? operadorDefault = filter.getStringDefaultOperator()
						: Operators.equal();
						
				Operation operacao = new Operation(propriedade.generateDotNotation(), operadorDefault, propriedade.getValue());
				if (operacao.isValid()) {
					containerDefinitivo.addOperation(operacao);
				}
				
			}	
		}		
		
		return containerDefinitivo;
	}
	
	private OperationContainer extractValidOperations(Set<String> cachePropriedadesCustomizadas, 
			TIPO exemplo, OperationContainer containerRaiz) throws CloneNotSupportedException {
		
		// criando um container para receber as configuracoes definitivas do filtro
		// A utilização do clone protege a estrutura original das operações que serão realizadas
		OperationContainer containerDefinitivo = (OperationContainer) containerRaiz.clone();
		
		// aplicando a transformação para garantir que as customizações de estruturas sejam consideradas
		containerDefinitivo = containerDefinitivo.transform();
		
		// mantém no container definitivo apenas as operações válidas
		List<Operation> operacoesValidas = extractValidOperations(cachePropriedadesCustomizadas, exemplo, containerDefinitivo.getOperations());
		containerDefinitivo.setOperations(operacoesValidas);
		
		// executa o mesmo procedimento anterior, recursivamente, a cada um dos sub-containers existentes
		if (containerDefinitivo.getSubContainers() != null) {
			List<OperationContainer> containeres = new ArrayList<OperationContainer>();
			for (OperationContainer subContainer : containerDefinitivo.getSubContainers()) {
				OperationContainer subContainerDefinitivo = extractValidOperations(cachePropriedadesCustomizadas, exemplo, subContainer);
				containeres.add(subContainerDefinitivo);
			}
			
			// atualiza o container com a lista de subcontainers atualizados com as operações válidas
			containerDefinitivo.setSubContainers(containeres);
		}
		
		// ao final, devemos ter o container definitivo contendo todas as operações válidas
		// existentes na estrutura encontrada no container transformado
		return containerDefinitivo;
	}
	
	private List<Operation> extractValidOperations(Set<String> cachePropriedadesCustomizadas, 
			TIPO exemplo,  List<Operation> operacoes) {
		
		// operacoes válidas extraidas do container raiz do filtro
		List<Operation> operacoesValidas = new ArrayList<Operation>();
		
		if (operacoes != null) {

			for (Operation operacao : operacoes) {
				
				// caso a operação não tenha valor, utiliza o valor informado na entidade filtro
				if (operacao.getOperator() != null
						&& !operacao.getOperator().isNullValueAllowed() 
						&& (operacao.getValues() == null || operacao.getValues().length == 0)) {
					
					Object valorAtributoOperacao = ReflectionUtil.getValue(exemplo, operacao.getProperty());
					operacao.setValor(valorAtributoOperacao);
				}
				
				// processa se a operacao é válida
				if (operacao.isValid()) {
					operacoesValidas.add(operacao);
					
					if (operacao.getProperty() != null) {
						cachePropriedadesCustomizadas.add(operacao.getProperty());
					}	
					
				}
				
			}
		}
		
		return operacoesValidas;
	}
	
	/**
	 * Aplica no Criteria as operações configuradas no container. São consideradas as operadores do container e
	 * de seus subcontaineres recursivamente. 
	 */
	private void applyOperations(OperationContainer containerValido) {
		// esta juncao representa o container, e conterá todas as operacoes deste container e de todos
		// os seus subcontainers
		Criterion grupoOperacoesContainerRaiz = applyOperations(null, containerValido);
		criteria.add(grupoOperacoesContainerRaiz);
	}	
	
	/**
	 * Cria as operações de um container, organizando-as em uma Junção, e adiciona na junção do container pai. Em seguida
	 * realiza o mesmo procedimento de forma recursiva em todos os subcontainers encontrados.
	 * @param criteria Criteria para operações necessárias de configuração.
	 * @param grupoOperacoesContainerPai Junção das operações do container pai.
	 * @param container Container para agrupamento das operações.
	 * @return Junção que representa as operações raiz do container informado.
	 */
	private Criterion applyOperations(Junction grupoOperacoesContainerPai, OperationContainer container) {
		// esta juncao representa o container, e conterá todas as suas operações
		Junction grupoOperacoesContainer = prepareOperations(container.getType(), container.getOperations());		

		if (container.getSubContainers() != null) {
			for (OperationContainer subContainer : container.getSubContainers()) {
				applyOperations(grupoOperacoesContainer, subContainer);
			}
		}
		
		// prepara o Criterion de retorno
		// verifica se há negação no container e aplica
		Criterion criterioGrupoOperacoesContainer = container.isNegated() ? Restrictions.not(grupoOperacoesContainer) : grupoOperacoesContainer;
		
		// adicona a junção no container pai 
		if (grupoOperacoesContainerPai != null) {
			grupoOperacoesContainerPai.add(criterioGrupoOperacoesContainer);
		}

		// se uma junção do container pai tiver sido informada, ela será o critério raiz
		// caso contrário, a primeira o critério do container atual será considerado raiz
		return grupoOperacoesContainerPai != null ? grupoOperacoesContainerPai : criterioGrupoOperacoesContainer;
	}	
	
	/**
	 * Agrupa as operações informadas em uma Junction.
	 * @param criteria Criteria para configurações necessárias (alias)
	 * @param tipoContainer Tipo do container que determinará o tipo da Junção criada.
	 * @param operacoes As operações que deverão ser agrupadas.
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Junction prepareOperations(ContainerType tipoContainer, List<Operation> operacoes) {
		Junction junction = null;
		if (operacoes != null) {
			// esta juncao representa o container, e conterá todas as operacoes deste container
			junction = getCriteriaContainer(tipoContainer);
			for (Operation operation : operacoes) {
				String propAlias = AliasUtil.createPropertyAlias(aliasCache, criteria, operation.getProperty());

				OperatorProcessor operatorProcessor = getOperatorProcessor(junction, operation);
				operatorProcessor.process(propAlias, operation.getOperator(), operation.getValues());
			}
		}	
		return junction;
	}

	@SuppressWarnings("rawtypes")
	protected OperatorProcessor getOperatorProcessor(Junction junction, Operation operation) {
		// recupera o processador responsável pelo operador
		Class<? extends OperatorProcessor> operatorProcessorType = getOperatorProcessorRepository().getProcessor(operation.getOperator().getClass());
		if (operatorProcessorType == null) {
			throw new QbeException("Não foi encontrado um processador registrado para o operador " + operation.getOperator().getClass());
		}
		
		OperatorProcessor operatorProcessor;
		try {
			
			// cria nova instância do processador, independente do tipo
			operatorProcessor = operatorProcessorType.newInstance();
			
			// TODO esta forma de configuracao de um processador Criteria nao está legal. Revisar em breve.
			if (CriteriaOperatorProcessorBase.class.isAssignableFrom(operatorProcessorType)) {
				CriteriaOperatorProcessorBase<?> critProcessor = (CriteriaOperatorProcessorBase<?>) operatorProcessor;
				critProcessor.setCriteria(this.criteria);
				critProcessor.setJunction(junction);
			}	
			return operatorProcessor;
		} catch (Exception e) {
			throw new QbeException("Ocorreu um problema ao tentar criar um OperatorProcessor: " + operatorProcessorType.getClass(), e);
		}
	}	
	
	private Junction getCriteriaContainer(ContainerType tipoContainer) {
		if (ContainerType.OR.equals(tipoContainer)) {
			return Restrictions.disjunction(); // aplica OR a todas as restricoes
		} else { 
			return Restrictions.conjunction(); // aplica AND como default
		}
	}
	
	/**
	 * Adiciona as configurações de paginação ao criteria.
	 * @param criteria Criteria para configuração
	 * @param paginacao Configurações de paginação na base de dados.
	 */
	protected void applyPaging(Pagination paginacao) {
		if (paginacao != null) {
			if (paginacao.getMaxResult() != null && paginacao.getMaxResult() > 0) {
				int optmisticSearch = paginacao.getOptmisticIndex() <= 0 ? 1 : paginacao.getOptmisticIndex();
				criteria.setMaxResults(paginacao.getMaxResult() * optmisticSearch);
			}
			
			if (paginacao.getFirstResult() != null && paginacao.getFirstResult() >= 0) {
				criteria.setFirstResult(paginacao.getFirstResult());
			}
		}
			
		
	}
	
	/**
	 * Adiciona as ordenações do filtro ao criteria.
	 * @param criteria Criteria para configuracao.
	 * @param orderConfigs Ordenações para configuração do criteria.
	 */
	protected void applyOrderings(List<SortConfig> orderConfigs) {
		if (orderConfigs != null) {
			for (SortConfig ordenacao : orderConfigs) {
				if (ordenacao.getProperty() != null) {
					
					String property = AliasUtil.createPropertyAlias(aliasCache, criteria, ordenacao.getProperty(), ordenacao.getJoinType());
					
					if (ordenacao.isAscendant()) {
						criteria.addOrder(Order.asc(property));
					} else {
						criteria.addOrder(Order.desc(property));
					}
				
				}	
			}
		}
	}

	protected OperatorProcessorRepository getOperatorProcessorRepository() {
		return operatorProcessorRepository;
	}

}
