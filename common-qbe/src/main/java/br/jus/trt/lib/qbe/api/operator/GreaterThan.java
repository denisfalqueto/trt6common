package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador ">";
 */
@SuppressWarnings("serial")
public class GreaterThan extends OperatorBase<Object> {

	/** Configura a operação para funcionar como "maior igual" */
	private boolean greaterEqual;
	
	/**
	 * Construtor default. Configura maiorIgual=false;
	 */
	public GreaterThan() {
		this(false);
	}
	
	/**
	 * @param greaterThan caso true, ativa a operação para trabalhar como "maior igual"
	 */
	public GreaterThan(boolean greaterThan) {
		this.greaterEqual = greaterThan;
	}
	
	
	public boolean isGreaterEqual() {
		return greaterEqual;
	}

	public void setGreaterEqual(boolean greaterEqual) {
		this.greaterEqual = greaterEqual;
	}

}
