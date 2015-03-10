package br.jus.trt.lib.qbe.api;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.jus.trt.lib.qbe.api.exception.CloneException;
import br.jus.trt.lib.qbe.util.StringUtil;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.StringPath;

/**
 * Classe que representa uma operação de restrição a ser aplicação em uma consulta QBE.
 * Indica qual operador deverá ser aplicado sobre qual propriedade
 * @author augusto
 */
@SuppressWarnings({"serial"})
public class Operation implements Serializable, Cloneable {

        private Logger log = LogManager.getLogger();
        
	/** Propriedade onde deverá ser utilizada na operação */
	private String property;

	/** Operador que deverá ser utilizado na operação */
	private Operator<?> operator;
	
	/** Valores a serem aplicadores na operação */
	private Object[] values;

	/**
	 * Default
	 */
	public Operation() { }
	
	/**
	 * Cria uma configuração de operação.
	 * @param property Propriedade que sofrerá a operação na consulta.
	 * @param operator Operador a ser aplicado sobre a propriedade.
	 * @param values Valores a serem aplicados com o operador.
	 */
	public Operation(String property, Operator<?> operator, Object...values) {
		super();
                log.entry(property, operator, values);
		this.property = property;
		this.operator = operator;
		this.values = values;
	}
	
	/**
	 * Cria uma configuração de operação.
	 * @param property Propriedade que sofrerá a operação na consulta.
	 * @param operator Operador a ser aplicado sobre a propriedade.
	 */
	public Operation(String property, Operator<?> operator) {
		super();
                log.entry(property, operator);
		this.property = property;
		this.operator = operator;
	}

	
	/**
	 * Cria uma configuração de operação.
	 * @param property Propriedade que sofrerá a operação na consulta.
	 * @param operator Operador a ser aplicado sobre a propriedade.
	 * @param values Valores a serem aplicados com o operador.
	 */
	@SuppressWarnings("rawtypes")
	public Operation(Path property, Operator<?> operator, Object...values) {
		super();
        log.entry(property, operator, values);
                
		this.property = StringUtil.getStringPath(property);
		this.operator = operator;
		this.values = values;
	}
	
	/**
	 * Cria uma configuração de operação.
	 * @param property Propriedade que sofrerá a operação na consulta.
	 * @param operator Operador a ser aplicado sobre a propriedade.
	 */
	@SuppressWarnings("rawtypes")
	public Operation(Path property, Operator<?> operator) {
		super();
                log.entry(property, operator);
		this.property = StringUtil.getStringPath(property);
		this.operator = operator;
	}

	
	/**
	 * Verifica se esta operação possui toda a configuração necessária para ser executada.
	 * @return true se for válida.
	 */
	public boolean isValid() {
                log.entry();
		boolean valid = true;
		
		// se a operação não permite valores nulos, verifica se os  valores informados são válidos
                log.debug("Analisando validade dos operadores");
		if (this.getOperator() != null
				&& !this.getOperator().isNullValueAllowed() 
				&& this.getValues() != null) {
			
			// se o valor representar um array vazio, é inválida
			if (this.getValues().length == 0) {
                                log.debug("Array vazio");
				valid = false;
			} else {
				// se algum dos valores do array for null ou String vazia, é inválida	
				for (Object valor : this.getValues()) {
					if (valor == null 
							|| ((valor instanceof String) && valor.toString().trim().equals(""))) {
                                                log.debug("Um dos valores é vazio");
						valid = false;
						break;
					}
				}
			}	
		}
		return log.exit(valid);
	}
	
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Operator<?> getOperator() {
		return operator;
	}

	public void setOperator(Operator<?> operator) {
		this.operator = operator;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}
	
	public void setValor(Object value) {
		values = new Object[] {value};
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {

		try {
			Operation clone = this.getClass().newInstance();
			clone.operator = (Operator<?>) this.operator.clone();
			clone.property = this.property;
			clone.values = this.values;
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloneNotSupportedException("Falha ao tentar realizar clone da operação");
		}
		
	}
	
	/**
	 * Apenas um wrapper tipado para o método clone();
	 * @return Clone deste objeto
	 */
	protected Operation typedClone() {
		try {
			return (Operation) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new CloneException(e);
		}
	}
	
	@Override
	public String toString() {
		return property + " " + operator + " " + values;
	}
}
