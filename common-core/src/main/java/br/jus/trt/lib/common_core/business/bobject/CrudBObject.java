package br.jus.trt.lib.common_core.business.bobject;

import java.lang.reflect.Field;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.exception.BusinessException;
import br.jus.trt.lib.common_core.integration.persistence.Dao;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.Operation;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.util.ReflectionUtil;


/**
 * Classe de negócio com operações direcionadas para entidades com ciclo de vida do tipo CRUD.
 * @author augusto
 *
 * @param <ENTITY> Tipo da entidade associada.
 */
@SuppressWarnings("serial")
public abstract class CrudBObject <ENTITY extends Entity<?>> extends QuerierObjectBase<ENTITY>{


	public CrudBObject(Dao<ENTITY> dao, QBERepository qbeRepository) {
		super(dao, qbeRepository);
	}

	/**
	 * Método responsável por excluir uma entidade.
	 * @param entity Entidade que será excluída.
	 */
	public void delete(ENTITY entity) {
		validateDelete(entity);
		getDao().delete(entity);
	}
	
	/**
	 * Exclui uma entidade da base de dados.
	 * @param id Identificador da entidade a ser excluída.
	 */
	public void delete(Object id) {
		ENTITY entidade = getDao().find(id);
		validateDelete(entidade);
		getDao().delete(entidade);
	}
	
	/**
	 * Atualiza os dados de uma entidade na base de dados.
	 * @param entity Objeto com dados atualizados.
	 */
	public void update(ENTITY entity) {
		validateUpdate(entity);
		validateSave(entity);
		getDao().update(entity);
	}
	
	/**
	 * Insere uma entidade na base de dados.
	 * @param entity Entidade que será inserida.
	 */
	public void insert(ENTITY entity) {
		validateInsert(entity);
		validateSave(entity);
		getDao().insert(entity);
	}

	/**
	 * Insere ou atualiza uma entidade na base de dados. O tipo de operação a ser realizada será verificada
	 * no momento da execução através do método {@link CrudBObject#isInsertion(Entity)}.
	 * @param entity Entidade que será excluída.
	 */
	public void save(ENTITY entity) {
		if (isInsertion(entity)) {
			insert(entity);
		} else {
			update(entity);
		}
	}
	
	/**
	 * Verifica se o objeto está preenchido para inclusão ou alteração. Por default,
	 * verifica se o ID está preenchido, considerando para inclusão entidades com ID=null e chave gerada por sequence.
	 * @param entity Entidade para verificação.
	 * @return true caso esteja preenchido para inclusão, false para alteração.
	 */
	protected boolean isInsertion(ENTITY entity) {
		return entity.getId() == null;	
	}
	
	/**
	 * Realiza validação de um conjunto de campos que deve ser unique. 
	 * @param entity Entidade a ser validada.
	 * @param exception Exceção de Negócio/Domínio a ser disparada caso a restrição de integridade não seja satisfeita
	 * @param uniqueFields Nome dos campos da entidade que devem ser unique (em conjunto).
	 */
	protected void validateUnique(ENTITY entity, BusinessException exception, String...uniqueFields) { 
		validateUnique(entity.getClass(), entity, exception, uniqueFields);
	}
	
	/**
	 * Realiza validação de um conjunto de campos que deve ser unique. 
	 * @param clazz Classe base da entidade a ser validada (útil para quando existir um hierarquia de classes e se deseja pesquisar pela classe base)
	 * @param entity Entidade a ser validada.
	 * @param exception Exceção de Negócio/Domínio a ser disparada caso a restrição de integridade não seja satisfeita
	 * @param uniqueFields Nome dos campos da entidade que devem ser unique (em conjunto).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void validateUnique(Class<? extends Entity> clazz, Entity<?> entity, BusinessException exception, String...uniqueFields) { 
		QBEFilter<?> filtro = new QBEFilter(clazz);

		if (uniqueFields != null  && uniqueFields.length > 0) {
			for (String field : uniqueFields) {
				Object value = ReflectionUtil.getValue(entity, field);
				filtro.addAnd(new Operation(field, Operators.equal(), value));	
			}
			if (entity.getId() != null) {			
				Field idField = getIdField((Class<? extends ENTITY>)clazz);
				filtro.addAnd(new Operation(idField.getName(), Operators.notEqual(), entity.getId()));	
			}

			if (getQbeRepository().count(filtro) > 0) {
				if (exception == null) {
					StringBuilder message = new StringBuilder();
					message.append("Já existe a entidade ").append(clazz.getSimpleName()).append(" com o(s) campo(s) ");
					for (int i = 0; i < uniqueFields.length; i++) {
						message.append(uniqueFields[i]);
						if (i < uniqueFields.length - 1) {
							message.append(",");
						}
					}
					message.append(" informado(s).");
					throw new BusinessException(null, message.toString());
				} else {
					throw exception;
				}
			}		
		}
	}

	
	/**
	 * Ponto de extensão para realização de validação da entidade executada antes da operação salvar. 
	 * @param entity Entidade a ser validada.
	 */
	public void validateSave(ENTITY entity) {}
	
	/**
	 * Ponto de extensão para realização de validação da entidade executada antes da operação inserir. 
	 * @param entity Entidade a ser validada.
	 */
	public void validateInsert(ENTITY entity) {}
	
	/**
	 * Ponto de extensão para realização de validação da entidade executada antes da operação atualizar. 
	 * @param entity Entidade a ser validada.
	 */
	public void validateUpdate(ENTITY entity) {}
	
	/**
	 * Ponto de extensão para realização de validação da entidade executada antes da operações de exclusão. 
	 * @param entity Entidade a ser validada.
	 */
	public void validateDelete(ENTITY entity) {}
}
