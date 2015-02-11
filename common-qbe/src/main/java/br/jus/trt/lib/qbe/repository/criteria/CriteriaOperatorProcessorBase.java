package br.jus.trt.lib.qbe.repository.criteria;

import java.lang.reflect.Array;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.operator.OperatorBase;

/**
 * Implementação básica de um processador de {@link Operator}.
 * @author augusto
 *
 * @param <VALOR>
 */
@SuppressWarnings("serial")
public abstract class CriteriaOperatorProcessorBase<VALOR> implements OperatorProcessor<VALOR> {

        private Logger log = LogManager.getLogger();
    
	private Criteria criteria;
	
	private Junction junction;

	/**
	 * @see OperatorProcessor#process(String, Operator, Object...)
	 */
	@Override
	public void process(String property, Operator<VALOR> operator, VALOR...values) {
                log.entry(property, operator, values);
		validate(property, operator, values);
		executeOperation(property, operator, castToGenericArray(values));
	}
	
	/**
	 * Realiza a validação dos parâmetros de entrada.
	 * @param propriedade Propriedade onde será executado o operador.
	 * @param operator Operador que está sendo processado.
	 * @param valores Valores que serão aplicados não operação.
	 */
	protected void validate(String propriedade, Operator<VALOR> operator, VALOR[] valores) {
                log.entry(propriedade, operator, valores);
		int valoresObrigatorios = operator.getMandatoryValuesNumber();
		if (valoresObrigatorios > 0 && (valores == null || valores.length < valoresObrigatorios)) {
			throw log.throwing(new OperatorException("O operador " + this.getClass().getSimpleName() + " exige a informação de pelo " +
					"menos " + valoresObrigatorios + " valores."));
		}
	}
	
	/**
	 * Processa de fato a inclusão da restrição no criteria.
	 * @param juncaoContainer Juncao para todas as propriedades do container onde a propriedade foi configurada. Adicionando a restrição
	 * @param propriedade Propriedade que receberá a restrição
	 * @param operator Operador sendo processado.
	 * @param valores Valor a ser aplicado sobre a restrição
	 */
	protected abstract void executeOperation(String propriedade, Operator<VALOR> operator, VALOR...valores);

	/**
	 * Método que cria uma array de tipo genérico compatível com este operador.
	 * Artifício para permitir o funcionamento correto do Generics em operadores.
	 * 
	 * Java não permite cast de arrays, portanto é necessário criar um novo array fazendo cast
	 * objeto por objeto.
	 * 
	 * @see OperatorBase#VA
	 * @param valor Valor a ser incluído no array.
	 * @return Array de tipo compatível com este objeto, contento o valor informado.
	 */
	@SuppressWarnings("unchecked")
	protected VALOR[] castToGenericArray(Object[] valores) {
                log.entry();
		try {
			VALOR[] valoresTipo = null;
			if (valores != null && valores.length > 0) {
				valoresTipo = (VALOR[]) Array.newInstance(valores[0].getClass(), valores.length);
				
				for (int i = 0; i < valores.length; i++) {
					valoresTipo[i] = (VALOR) valores[i];
				}
			}
			return valoresTipo;
		} catch (ClassCastException e) {
			throw new OperatorException("O tipo do valor informado não é compatível com este operador.", e);
		}
	}
	
	protected Criteria getCriteria() {
		return criteria;
	}
	
	protected Junction getJunction() {
		return junction;
	}

	protected void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	protected void setJunction(Junction junction) {
		this.junction = junction;
	}
	
}
