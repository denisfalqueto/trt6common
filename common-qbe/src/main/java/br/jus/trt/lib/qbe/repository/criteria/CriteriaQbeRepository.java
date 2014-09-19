package br.jus.trt.lib.qbe.repository.criteria;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;

import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.Identifiable;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.api.QBERepository;

/**
 * Implementa uma solução genérica e dinâmica para consultas, baseada no conceito de QBE (Query by Example).
 * Esta solução constrói dinamicamente uma QUERY baseada e um objeto preenchido com informações de consulta. 
 * Ele é capaz de analisar as propriedades do objeto, determinar quais são determinantes para consulta, construí-la e
 * executá-la.
 * 
 * @author augusto
 */
@SuppressWarnings("serial")
public class CriteriaQbeRepository implements QBERepository {

	private EntityManager entityManager;
	private OperatorProcessorRepository processorRepository;
	
	/**
	 * Construtor.
	 * @param entityManager EntityManager a ser utilizado nas operações com este QBE
	 * @param processorRepository Repositório com os processadores que determinam as operações suportadas.
	 */
	public CriteriaQbeRepository(EntityManager entityManager, OperatorProcessorRepository processorRepository) {
		this.entityManager = entityManager;
		this.processorRepository = processorRepository;
	}
	
	/**
	 * Realiza consulta dinâmica a partir da análise das propriedades do filtro recebido.
	 * @param filter Objeto preparado com dados de configuração para a geração dinâmica de uma consulta.
	 * @return Resultado da consulta de acordo com os parâmetros de entrada.
	 */
	public <TIPO extends Identifiable> List<TIPO> search(Filter<? extends TIPO> filter){
			CriteriaQBEProcessor<TIPO> processador = createQBEProcessor(filter);
			return processador.search();
	}
	
	/**
	 * Realiza consulta dinâmica a partir da análie das propriedades do filtro recebido, retornando o total de registros encontrados.
	 * @param filter Objeto preparado com dados de configuração para a geração dinâmica de uma consulta.
	 * @return Total de registros encontrados de acordo com o filtro informado.
	 */
	public <TIPO extends Identifiable> long count(Filter<? extends TIPO> filter){
			CriteriaQBEProcessor<TIPO> processador = createQBEProcessor(filter);
			return processador.count();
	}

	/**
	 * @param filter Parâmetro necessário para criação do processador de QBE
	 * @return Nova instância de um {@link CriteriaQBEProcessor} para processar uma operação de consulta isoladamente.
	 */
	protected <TIPO extends Identifiable> CriteriaQBEProcessor<TIPO> createQBEProcessor(Filter<? extends TIPO> filter) {
		return new CriteriaQBEProcessor<TIPO>(processorRepository, getSession(), filter);
	}	

	protected Session getSession() {
		return  entityManager != null ? (Session) entityManager.getDelegate() : null;
	}

	@Override
	public OperatorProcessorRepository getOperatorRepository() {
		return this.processorRepository;
	}
}
