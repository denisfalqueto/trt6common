package br.jus.trt.lib.qbe.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.jus.trt.lib.qbe.api.exception.CloneException;
import br.jus.trt.lib.qbe.util.ReflectionUtil;


/**
 * Representa escopos para agrupamento de operações. Ex: AND(operacoes), OR(operacoes)
 * @author augusto
 */
@SuppressWarnings("serial")
public class OperationContainer implements Cloneable, Serializable {

	/** Tipos de container suportados */
	public enum ContainerType {AND, OR};
	
	/** Operações contidas neste container */
	private List<Operation> operations;
	
	/** Lista de outros container internos */
	private List<OperationContainer> subContainers;

	/** Tipo deste container */
	private ContainerType type;
	
	/** Permite realizar a negação de toda a estrutura de operações do container */
	private boolean negated;

	/**
	 * @return Um container do tipo AND
	 */
	public static final OperationContainer and() {
		return new OperationContainer(ContainerType.AND);
	}
	
	/**
	 * @return Um container do tipo OR
	 */
	public static final OperationContainer or() {
		return new OperationContainer(ContainerType.OR);
	}
	
	/**
	 * default
	 */
	public OperationContainer() {
		clearOperations();
		clearSubContainers();
	}
	
	/**
	 * @param type Tipo deste container de operações
	 */
	public OperationContainer(ContainerType type) {
		this();
		this.type = type;
	}

	/**
	 * Adiciona operações
	 * @param operations Operações.
	 */
	public OperationContainer addOperation(Operation...operations) {
		if (operations != null) {
			for (int i = 0; i < operations.length; i++) {
				Operation operacao = operations[i];
				this.operations.add(operacao);
			}
		}
		return this;
	}
	
	/**
	 * @param operations Operações a serem adicionadas ao container.
	 */
	public OperationContainer addOperations(List<Operation> operations) {
		this.operations.addAll(operations);
		return this;
	}
	
	/**
	 * @param container Container a ser adicionado como subcontainer deste.
	 */
	public OperationContainer addSubContainer(OperationContainer container) {
		this.subContainers.add(container);
		return this;
	}
	
	/**
	 * Cria e adiciona um subcontainer de operações OR no container raiz deste filtro.
	 * @param operations Operações que deverão fazer parte do subcontainer.
	 * @return Referência para o subcontainer criado.
	 */
	public OperationContainer addOr(Operation...operations) {
		OperationContainer or = OperationContainer.or();
		or.addOperation(operations);
		addSubContainer(or);
		return or;
	}
	
	/**
	 * Cria e adiciona um subcontainer de operações AND no container raiz deste filtro.
	 * @param operations Operações que deverão fazer parte do subcontainer.
	 * @return Referência para o subcontainer criado.
	 */
	public OperationContainer addAnd(Operation...operations) {
		OperationContainer and = OperationContainer.and();
		and.addOperation(operations);
		addSubContainer(and);
		return and;
	}	
	
	public List<Operation> getOperations() {
		return operations;
	}

	public ContainerType getType() {
		return type;
	}

	public void setType(ContainerType type) {
		this.type = type;
	}

	public List<OperationContainer> getSubContainers() {
		return subContainers;
	}

	/**
	 * Permite especializar a implementação do container, através da reorganização ou re-estruturação dos operadores e subcontainers.
	 * @return Referência para o container especializado. Retorna este próprio container por default.
	 */
	public OperationContainer transform() {
		return this;
	}
	
	/**
	 * Descarta as operações configuradas atualmente e considera apenas as informadas através do parâmetro.
	 * @param operations Lista de operações que substituirá as operações atualmente configuradas neste container.
	 */
	public void setOperations(List<Operation> operations) {
		clearOperations();
		if (operations != null) {
			for (Operation operation : operations) {
				addOperation(operation);
			}
		}
	}

	protected void clearOperations() {
		this.operations = new ArrayList<Operation>();
	}

	/**
	 * Descarta os subcontainers configurados atualmente e considera apenas os informados através do parâmetro.
	 * @param operations Lista de subconteiners que substituirá os atualmente configurados.
	 */
	public void setSubContainers(List<OperationContainer> subContainers) {
		clearSubContainers();
		if (subContainers != null) {
			for (OperationContainer subContainer : subContainers) {
				addSubContainer(subContainer);
			}
		}
	}
	
	protected void clearSubContainers() {
		this.subContainers = new ArrayList<OperationContainer>();
	}
	
	public boolean isNegated() {
		return negated;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}

	/** Determina que o container deve ser negado, atribuindo uma negação à estrutura lógica montada pelas operações e subcontainers. 
	 */
	public OperationContainer negate() {
		this.setNegated(true);
		return this;
	}
	
	public Object clone() throws CloneNotSupportedException {
		try {
			OperationContainer clone = this.getClass().newInstance();
			ReflectionUtil.copyProperties(this, clone);

			// clonando cada operacao
			if (this.operations != null) {
				clone.setOperations(new ArrayList<Operation>());
				for (Operation operacao : this.operations) {
					clone.addOperation((Operation) operacao.clone());	
				}
			} 			
			
			// clonando cada subcontainer
			if (this.subContainers != null) {
				clone.setSubContainers(new ArrayList<OperationContainer>());
				for (OperationContainer subcontainer : this.subContainers) {
					clone.addSubContainer((OperationContainer) subcontainer.clone());	
				}
			}
			
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloneNotSupportedException("Falha ao tentar realizar clone do container");
		}
		
	}
	
	/**
	 * Apenas um wrapper tipado para o método clone();
	 * @return Clone deste objeto
	 */
	protected OperationContainer typedClone() {
		try {
			return (OperationContainer) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new CloneException(e);
		}
	}
}
